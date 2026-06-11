package com.think_different.think_different.calendar.dto;

import com.think_different.think_different.calendar.entity.Calendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarResponseDto {

    private Long id;
    private String title;
    private String memo;
    private LocalDate scheduleDate;
    private LocalTime startDate;
    private LocalTime endDate;

    public static CalendarResponseDto fromCalendar(Calendar calendar) {
        return CalendarResponseDto.builder()
                .id(calendar.getId())
                .title(calendar.getTitle())
                .memo(calendar.getMemo())
                .scheduleDate(calendar.getScheduleDate())
                .startDate(calendar.getStartTime())
                .endDate(calendar.getEndTime())
                .build();
    }
}
