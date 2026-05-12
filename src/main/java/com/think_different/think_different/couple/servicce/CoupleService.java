package com.think_different.think_different.couple.servicce;

import com.think_different.think_different.couple.domain.InviteCode;
import com.think_different.think_different.couple.dto.CoupleStatusResponseDto;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.couple.repository.InviteCodeRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static java.lang.System.in;

@Service
@Transactional
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleMemberRepository coupleMemberRepository;
    private final InviteCodeRepository inviteCodeRepository;

    public boolean isConnected(Member member) {

        return coupleMemberRepository.existsByMember(member);
    }

    public void createInviteCode(Member member) {

        if (isConnected(member)) {
            throw new IllegalArgumentException("이미 연결된 커플 사용자입니다.");
        }

        boolean existsInviteCode = inviteCodeRepository.existsByMemberAndUsedIsFalse(member);

        if (existsInviteCode) {
            return;
        }

        String code = generateInviteCode();

        InviteCode inviteCode = InviteCode.builder()
                .member(member)
                .code(code)
                .used(false)
                .build();

        inviteCodeRepository.save(inviteCode);
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
}
