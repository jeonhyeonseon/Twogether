package com.think_different.think_different.couple.servicce;

import com.think_different.think_different.couple.dto.CoupleStatusResponseDto;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleMemberRepository coupleMemberRepository;

    public boolean getCoupleStatus(Member member) {

        return coupleMemberRepository.existsByMember(member);
    }
}
