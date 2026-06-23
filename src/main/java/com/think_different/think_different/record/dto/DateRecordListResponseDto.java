package com.think_different.think_different.record.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRecordListResponseDto {

    private Long id;
    private String title;
    private LocalDate dateRecordDate;
    private String memo;
    private String thumbnailImageUrl;

}
