package com.think_different.think_different.member.service;

import com.think_different.think_different.common.file.FileUploadService;
import com.think_different.think_different.couple.servicce.CoupleService;
import com.think_different.think_different.member.dto.MemberRequestDto;
import com.think_different.think_different.member.dto.MemberUpdateRequestDto;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.member.repository.EmailVerificationRepository;
import com.think_different.think_different.member.repository.MemberRepository;
import com.think_different.think_different.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoupleService coupleService;
    private final TransactionRepository transactionRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final FileUploadService fileUploadService;

    private static final String PROFILE_IMAGE_DIRECTORY = "profile";

    public void joinMember(MemberRequestDto memberRequestDto) {

        // 아이디 중복 체크
        if(memberRepository.existsByLoginId(memberRequestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 체크
        if(memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 엔터티 변환
        Member member = memberRequestDto.toMember();
        // 비밀번호 암호화
        member.encodePassword(passwordEncoder);
        // 저장
        memberRepository.save(member);
    }

    // 아이디 중복 체크
    public boolean existsLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    // 이메일 중복 체크
    public boolean existsEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 아이디 찾기
    public String findLoginIdByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(Member::getLoginId)
                .orElseThrow(() -> new IllegalArgumentException("가입된 이메일이 아닙니다."));
    }

    // 비밀번호 찾기 - 재설정 대상 회원 조회
    public Long findMemberIdForPasswordReset(String loginId, String email) {
        return memberRepository.findByLoginIdAndEmail(loginId, email)
                .map(Member::getId)
                .orElseThrow(() -> new IllegalArgumentException("아이디와 이메일이 일치하는 회원이 없습니다."));
    }

    // 비밀번호 재설정
    public void resetPassword(Long memberId, String newPassword) {
        Member member = getMember(memberId);
        member.changePassword(passwordEncoder.encode(newPassword));
    }

    // 마이페이지 - 내 정보 조회 (세션의 Member는 로그인 시점 스냅샷이므로 DB에서 다시 조회)
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }

    // 마이페이지 - 내 정보 수정 (이름, 전화번호 + 요청 시 비밀번호를 한 트랜잭션에서 함께 변경)
    public Member updateMyInfo(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto) {
        Member member = getMember(memberId);

        if (memberUpdateRequestDto.isPasswordChangeRequested()) {
            // 현재 비밀번호 확인
            if (!StringUtils.hasText(memberUpdateRequestDto.getCurrentPassword())
                    || !passwordEncoder.matches(memberUpdateRequestDto.getCurrentPassword(), member.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호 확인 일치
            if (!StringUtils.hasText(memberUpdateRequestDto.getNewPassword())
                    || !memberUpdateRequestDto.getNewPassword().equals(memberUpdateRequestDto.getNewPasswordConfirm())) {
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 암호화 후 변경
            member.changePassword(passwordEncoder.encode(memberUpdateRequestDto.getNewPassword()));
        }

        member.updateInfo(memberUpdateRequestDto.getName(), memberUpdateRequestDto.getPhone());

        // 프로필 사진 - 새 사진 업로드 또는 기본 이미지로 (이전 파일은 삭제)
        String oldProfileImageUrl = member.getProfileImageUrl();
        if (memberUpdateRequestDto.hasProfileImage()) {
            member.updateProfileImage(fileUploadService.uploadImage(memberUpdateRequestDto.getProfileImage(), PROFILE_IMAGE_DIRECTORY));
            fileUploadService.delete(oldProfileImageUrl);
        } else if (Boolean.TRUE.equals(memberUpdateRequestDto.getRemoveProfileImage())) {
            member.updateProfileImage(null);
            fileUploadService.delete(oldProfileImageUrl);
        }

        return member;
    }

    // 마이페이지 - 현재 비밀번호 일치 여부
    public boolean matchesPassword(Long memberId, String rawPassword) {
        return passwordEncoder.matches(rawPassword, getMember(memberId).getPassword());
    }

    // 회원 탈퇴 - 공유 데이터(일정/지출/기록/버킷리스트)는 유지, 개인정보와 개인 데이터만 정리
    public void withdraw(Long memberId, String rawPassword) {
        Member member = getMember(memberId);

        // 비밀번호 재확인
        if (!StringUtils.hasText(rawPassword) || !passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 커플이 연결돼 있으면 먼저 연결 해제 (커플 연결 해제 로직 재사용)
        if (coupleService.isConnected(member)) {
            coupleService.disconnectCouple(member);
        }

        // 사용하지 않은 초대 코드 만료, 개인 가계부/이메일 인증 이력 삭제
        coupleService.expireInviteCodes(member);
        transactionRepository.deleteByMember(member);
        emailVerificationRepository.deleteByEmail(member.getEmail());

        // 프로필 사진 파일 삭제
        fileUploadService.delete(member.getProfileImageUrl());

        // 개인정보 비식별화 + 탈퇴 상태 (비밀번호는 아무도 모르는 값으로)
        member.withdraw(passwordEncoder.encode(UUID.randomUUID().toString()));
    }
}
