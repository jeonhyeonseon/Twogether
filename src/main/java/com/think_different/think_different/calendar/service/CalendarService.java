package com.think_different.think_different.calendar.service;

import com.think_different.think_different.calendar.dto.CalendarRequestDto;
import com.think_different.think_different.calendar.dto.CalendarResponseDto;
import com.think_different.think_different.calendar.entity.Calendar;
import com.think_different.think_different.calendar.repository.CalendarRepository;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final CoupleMemberRepository coupleMemberRepository;

    public List<CalendarResponseDto> getMonthlySchedules(Member member, YearMonth currentMonth) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        return calendarRepository.findByCoupleAndScheduleDateBetweenOrderByScheduleDateAsc(
                coupleMember.getCouple(),
                startDate,
                endDate
        ).stream().map(CalendarResponseDto::fromCalendar).toList();
    }

    public Long registerSchedule(Member member, CalendarRequestDto calendarRequestDto) {
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Calendar calendar = calendarRequestDto.toCalendar(
                coupleMember.getCouple(),
                member
        );

        return calendarRepository.save(calendar).getId();
    }

    public void updateSchedule(Member member, Long calendarId, CalendarRequestDto calendarRequestDto) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        if (!calendar.getCouple().getId().equals(couple.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        calendar.updateCalendar(
                calendarRequestDto.getTitle(),
                calendarRequestDto.getMemo(),
                calendarRequestDto.getScheduleDate(),
                calendarRequestDto.getStartTime(),
                calendarRequestDto.getEndTime()
        );
    }

    public void deleteSchedule(Member member, Long calendarId) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        if (!calendar.getCouple().getId().equals(couple.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        calendarRepository.delete(calendar);
    }
}
