package com.think_different.think_different.calendar.dto;

import com.think_different.think_different.calendar.entity.Calendar;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.member.entity.Member;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class CalendarRequestDto {

    private String title;
    private String memo;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public Calendar toCalendar(Couple couple, Member createdBy) {
        return Calendar.builder()
                .couple(couple)
                .createdBy(createdBy)
                .title(title)
                .memo(memo)
                .scheduleDate(scheduleDate)
                .startTime(startTime)
                .endTime(endTime)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
