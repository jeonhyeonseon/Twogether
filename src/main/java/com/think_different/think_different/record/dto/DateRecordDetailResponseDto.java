package com.think_different.think_different.record.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DateRecordDetailResponseDto {

    private Long id;
    private String title;
    private LocalDate dateRecordDate;
    private String memo;
    private List<DateRecordImageResponseDto> images;

    private List<DateRecordExpenseResponseDto> expenses;
    private int totalExpenseAmount;

}
