package com.think_different.think_different.member.service;

import com.think_different.think_different.common.mail.EmailService;
import com.think_different.think_different.member.entity.EmailVerification;
import com.think_different.think_different.member.repository.EmailVerificationRepository;
import com.think_different.think_different.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRE_MINUTES = 5;
    private static final long RESEND_COOLDOWN_SECONDS = 60;
    private static final int MAX_SENDS_PER_DAY = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    // 인증코드 발송 (이메일 중복 체크 + 재발송 제한 포함)
    public void sendVerificationCode(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String code = issueCode(email);
        emailService.sendVerificationCode(email, code);
    }

    // 아이디 찾기 인증코드 발송 (가입된 이메일만)
    public void sendFindIdCode(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("가입된 이메일이 아닙니다.");
        }

        String code = issueCode(email);
        emailService.sendAccountFindCode(email, code);
    }

    // 비밀번호 찾기 인증코드 발송 (아이디 + 이메일이 일치하는 회원만)
    public void sendFindPasswordCode(String loginId, String email) {
        if (memberRepository.findByLoginIdAndEmail(loginId, email).isEmpty()) {
            throw new IllegalArgumentException("아이디와 이메일이 일치하는 회원이 없습니다.");
        }

        String code = issueCode(email);
        emailService.sendAccountFindCode(email, code);
    }

    // 아이디/비밀번호 찾기 인증코드 확인 (1회용 + 입력 실패 횟수 제한)
    // 실패 횟수 증가가 롤백되지 않도록 IllegalArgumentException 은 롤백 제외
    @Transactional(dontRollbackOn = IllegalArgumentException.class)
    public void verifyFindCode(String email, String code) {
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증코드를 먼저 발송해주세요."));

        if (emailVerification.isVerified()
                || emailVerification.getFailedAttemptCount() >= MAX_FAILED_ATTEMPTS) {
            throw new IllegalArgumentException("인증코드를 다시 발송해주세요.");
        }

        if (emailVerification.isExpired()) {
            throw new IllegalArgumentException("인증코드가 만료되었습니다. 다시 발송해주세요.");
        }

        if (!emailVerification.getCode().equals(code)) {
            emailVerification.increaseFailedAttempts();
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다.");
        }

        emailVerification.verify();
    }

    // 재발송 제한 확인 후 인증코드 생성/저장
    private String issueCode(String email) {
        LocalDateTime now = LocalDateTime.now();

        emailVerificationRepository.findTopByEmailOrderByIdDesc(email).ifPresent(last -> {
            long secondsSinceLastSend = java.time.Duration.between(last.getCreatedAt(), now).getSeconds();
            if (secondsSinceLastSend < RESEND_COOLDOWN_SECONDS) {
                throw new IllegalArgumentException(
                        (RESEND_COOLDOWN_SECONDS - secondsSinceLastSend) + "초 후에 다시 시도해주세요.");
            }
        });

        long sentToday = emailVerificationRepository.countByEmailAndCreatedAtAfter(email, now.minusDays(1));
        if (sentToday >= MAX_SENDS_PER_DAY) {
            throw new IllegalArgumentException("인증코드 발송 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
        }

        String code = generateCode();

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expiresAt(now.plusMinutes(CODE_EXPIRE_MINUTES))
                .verified(false)
                .createdAt(now)
                .build();
        emailVerificationRepository.save(emailVerification);

        return code;
    }

    // 인증코드 확인
    public void verifyCode(String email, String code) {
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증코드를 먼저 발송해주세요."));

        if (emailVerification.isExpired()) {
            throw new IllegalArgumentException("인증코드가 만료되었습니다. 다시 발송해주세요.");
        }

        if (!emailVerification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다.");
        }

        emailVerification.verify();
    }

    // 회원가입 제출 시점의 이메일 인증 완료 여부 재검증
    public boolean isVerified(String email) {
        return emailVerificationRepository.existsByEmailAndVerifiedTrue(email);
    }

    private String generateCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
