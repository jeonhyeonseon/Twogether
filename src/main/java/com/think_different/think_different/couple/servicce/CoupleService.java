package com.think_different.think_different.couple.servicce;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.domain.CoupleStatus;
import com.think_different.think_different.couple.domain.InviteCode;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.couple.repository.CoupleRepository;
import com.think_different.think_different.couple.repository.InviteCodeRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleMemberRepository coupleMemberRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final CoupleRepository coupleRepository;

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

    public void connectCouple(Member member, String code) {

        if (isConnected(member)) {
            throw new IllegalArgumentException("이미 연결된 사용자입니다.");
        }

        InviteCode inviteCode = inviteCodeRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대 코드입니다."));

        if (inviteCode.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 초대 코드입니다.");
        }

        Member inviter = inviteCode.getMember();

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
}
