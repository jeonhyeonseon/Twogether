package com.think_different.think_different.dashboard.service;

import com.think_different.think_different.calendar.dto.CalendarResponseDto;
import com.think_different.think_different.calendar.repository.CalendarRepository;
import com.think_different.think_different.common.file.FileUploadService;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.dto.CoupleInfoUpdateRequestDto;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.dashboard.dto.DashboardResponseDto;
import com.think_different.think_different.expense.domain.Expense;
import com.think_different.think_different.expense.repository.ExpenseRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final CoupleMemberRepository coupleMemberRepository;
    private final FileUploadService fileUploadService;
    private final CalendarRepository calendarRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardResponseDto getDashboard(Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        List<CoupleMember> coupleMemberList = coupleMemberRepository.findByCouple(couple);

        CoupleMember partnerCoupleMember = coupleMemberList.stream()
                .filter(coupleMember1 -> !coupleMember1.getMember().getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상대의 정보를 찾을 수 없습니다."));

        LocalDate coupleStartDate = couple.getStartDate();

        Long dDate = (coupleStartDate != null)
                ? (ChronoUnit.DAYS.between(coupleStartDate, LocalDate.now()) + 1)
                : null;

        LocalDate today = LocalDate.now();

        List<CalendarResponseDto> upcomingSchedules =
                calendarRepository
                        .findByCoupleAndScheduleDateGreaterThanEqualOrderByScheduleDateAsc(
                                couple,
                                today
                        )
                        .stream()
                        .limit(3)
                        .map(CalendarResponseDto::fromCalendar)
                        .toList();

        YearMonth currentMonth = YearMonth.now();

        LocalDate monthStartDate = currentMonth.atDay(1);
        LocalDate monthEndDate = currentMonth.atEndOfMonth();

        List<Expense> monthlyExpenses =
                expenseRepository.findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(
                        couple,
                        monthStartDate,
                        monthEndDate
                );

        int monthlyTotalAmount = monthlyExpenses.stream()
                .mapToInt(Expense::getAmount)
                .sum();

        int monthlyAverageAmount = monthlyExpenses.isEmpty()
                ? 0
                : monthlyTotalAmount / monthlyExpenses.size();

        int monthlyDateCount = 0;

        return DashboardResponseDto.builder()
                .memberName(member.getName())
                .partnerName(partnerCoupleMember.getMember().getName())
                .myNickname(coupleMember.getNickname())
                .partnerNickname(partnerCoupleMember.getNickname())
                .myProfileImageUrl(coupleMember.getProfileImageUrl())
                .partnerProfileImageUrl(partnerCoupleMember.getProfileImageUrl())
                .startDate(coupleStartDate)
                .dDay(dDate)
                .hasStartDate(coupleStartDate != null)
                .upcomingSchedules(upcomingSchedules)
                .monthlyTotalAmount(monthlyTotalAmount)
                .monthlyAverageAmount(monthlyAverageAmount)
                .monthlyDateCount(monthlyDateCount)
                .build();
    }

    public void updateCoupleInfo(Member member,
                                 CoupleInfoUpdateRequestDto coupleInfoUpdateRequestDto) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        List<CoupleMember> coupleMembers = coupleMemberRepository.findByCouple(couple);

        CoupleMember partnerCoupleMember = coupleMembers.stream()
                .filter(coupleMember1 -> !coupleMember1.getMember().getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상대의 정보를 찾을 수 없습니다."));

        couple.updateStartDate(coupleInfoUpdateRequestDto.getStartDate());

        String myProfileImageUrl = fileUploadService.upload(
                coupleInfoUpdateRequestDto.getMyProfileImage(),
                "couple-profile"
        );

        String partnerProfileImageUrl = fileUploadService.upload(
                coupleInfoUpdateRequestDto.getPartnerProfileImage(),
                "couple-profile"
        );

        coupleMember.updateDisplayInfo(
                coupleInfoUpdateRequestDto.getMyNickname(),
                myProfileImageUrl
        );

        partnerCoupleMember.updateDisplayInfo(
                coupleInfoUpdateRequestDto.getPartnerNickname(),
                partnerProfileImageUrl
        );
    }
}
