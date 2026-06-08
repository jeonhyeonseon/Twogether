package com.think_different.think_different.dashboard.service;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.dto.CoupleInfoUpdateRequestDto;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.dashboard.dto.DashboardResponseDto;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final CoupleMemberRepository coupleMemberRepository;

    public DashboardResponseDto getDashboard(Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        List<CoupleMember> coupleMemberList = coupleMemberRepository.findByCouple(couple);

        Member partner = coupleMemberList.stream()
                .map(CoupleMember::getMember)
                .filter(m -> !m.getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상대의 정보를 찾을 수 없습니다."));

        LocalDate startDate = couple.getStartDate();

        Long dDate = (startDate != null)
                ? (ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1)
                : null;

        return DashboardResponseDto.builder()
                .memberName(member.getName())
                .partnerName(partner.getName())
                .startDate(startDate)
                .dDay(dDate)
                .hasStartDate(startDate != null)
                .build();
    }

    public void updateCoupleInfo(Member member,
                                 CoupleInfoUpdateRequestDto coupleInfoUpdateRequestDto) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        couple.updateStartDate(coupleInfoUpdateRequestDto.getStartDate());
    }
}
