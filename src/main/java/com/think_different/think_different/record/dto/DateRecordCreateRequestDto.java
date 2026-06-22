package com.think_different.think_different.record.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRecordCreateRequestDto {

    private String title;
    private LocalDate dateRecordDate;
    private String memo;

}
