package com.think_different.think_different.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyExpenseDto {

    private String month;
    private Long amount;

    public MonthlyExpenseDto(Integer year, Integer month, Long amount) {
        this.month = String.format("%d-%02d", year, month);
        this.amount = amount;
    }

}
