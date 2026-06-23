package com.think_different.think_different.record.dto;

import lombok.Data;

@Data
public class DateRecordExpenseResponseDto {

    private Long id;
    private String content;
    private int amount;
    private String categoryName;

}
