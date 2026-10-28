package com.think_different.think_different.couple.servicce;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.domain.CoupleStatus;
import com.think_different.think_different.couple.domain.InviteCode;
import com.think_different.think_different.couple.dto.CoupleConnectionResponseDto;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.couple.repository.CoupleRepository;
import com.think_different.think_different.couple.repository.InviteCodeRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleMemberRepository coupleMemberRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final CoupleRepository coupleRepository;

    public boolean isConnected(Member member) {

        return coupleMemberRepository.existsConnectedByMember(member);
    }

    public String createInviteCode(Member member) {

        if (isConnected(member)) {
            throw new IllegalArgumentException("이미 연결된 커플 사용자입니다.");
        }

        boolean existsInviteCode = inviteCodeRepository.existsByMemberAndUsedIsFalse(member);

        if (existsInviteCode) {
            InviteCode existingCode = inviteCodeRepository.findByMemberAndUsedIsFalse(member).orElseThrow();

            return existingCode.getCode();
        }

        String code = generateInviteCode();

        InviteCode inviteCode = InviteCode.builder()
                .member(member)
                .code(code)
                .used(false)
                .build();

        inviteCodeRepository.save(inviteCode);

        return inviteCode.getCode();
    }

    private String generateInviteCode() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        SecureRandom secureRandom = new SecureRandom();

        String code;

        do {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                stringBuilder.append(
                        chars.charAt(
                                secureRandom.nextInt(chars.length())
                        )
                );
            }

            code = stringBuilder.toString();
        } while (inviteCodeRepository.existsByCode(code));

        return code;
    }

    public void connectCouple(Member member, String code) {

        if (isConnected(member)) {
            throw new IllegalArgumentException("이미 연결된 사용자입니다.");
        }

        InviteCode inviteCode = inviteCodeRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대 코드입니다."));

        if (inviteCode.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 초대 코드입니다.");
        }

        Member inviter = inviteCode.getMember();

        // 탈퇴한 회원의 초대 코드는 사용 불가
        if (inviter.isWithdrawn()) {
            throw new IllegalArgumentException("사용할 수 없는 초대 코드입니다.");
        }

        if (inviter.getId().equals(member.getId())) {
            throw new IllegalArgumentException("사용자의 초대 코드는 사용할 수 없습니다.");
        }

        if (isConnected(inviter)) {
            throw new IllegalArgumentException("초대한 사용자와 이미 연결되었습니다.");
        }

        Couple couple = Couple.builder()
                .coupleStatus(CoupleStatus.CONNECTED)
                .createdAt(LocalDateTime.now())
                .build();

        coupleRepository.save(couple);

        CoupleMember inviteCoupleMember = CoupleMember.builder()
                .couple(couple)
                .member(inviter)
                .joinedAt(LocalDateTime.now())
                .build();

        CoupleMember partnerCoupleMember = CoupleMember.builder()
                .couple(couple)
                .member(member)
                .joinedAt(LocalDateTime.now())
                .build();

        coupleMemberRepository.save(inviteCoupleMember);
        coupleMemberRepository.save(partnerCoupleMember);

        inviteCode.use();
    }

    public Member findPartner(Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findConnectedByMember(member).orElseThrow(() -> new IllegalArgumentException("정보가 없습니다."));

        Couple couple = coupleMember.getCouple();

        List<CoupleMember> coupleMembers = coupleMemberRepository.findByCouple(couple);

        return coupleMembers
                .stream()
                .map(CoupleMember::getMember)
                .filter(member1 -> !member1.getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상대가 존재하지 않습니다."));
    }

    public String getUsableInviteCode(Member member) {
        return inviteCodeRepository.findByMemberAndUsedIsFalse(member)
                .map(InviteCode::getCode)
                .orElse("");

    }

    // 마이페이지 - 현재 연결된 커플 정보 (연결 안 됐으면 empty)
    public Optional<CoupleConnectionResponseDto> getConnection(Member member) {
        return coupleMemberRepository.findConnectedByMember(member)
                .map(coupleMember -> CoupleConnectionResponseDto.builder()
                        .partnerName(findPartner(member).getName())
                        .connectedAt(coupleMember.getJoinedAt())
                        .build());
    }

    // 커플 연결 해제 (한쪽이 바로 해제, 공유 데이터는 삭제하지 않고 커플 상태만 DISCONNECTED 로 변경)
    // 회원 탈퇴 시에도 재사용
    public void disconnectCouple(Member member) {
        CoupleMember coupleMember = coupleMemberRepository.findConnectedByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("연결된 커플이 없습니다."));

        coupleMember.getCouple().disconnect();
    }

    // 회원 탈퇴 시 사용하지 않은 초대 코드 사용 처리 (탈퇴 회원과 연결되지 않도록)
    public void expireInviteCodes(Member member) {
        inviteCodeRepository.findByMemberAndUsedIsFalse(member).ifPresent(InviteCode::use);
    }
}
