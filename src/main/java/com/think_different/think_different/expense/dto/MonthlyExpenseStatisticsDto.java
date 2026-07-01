package com.think_different.think_different.expense.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyExpenseStatisticsDto {

    private String monthLabel;
    private int amount;
    private int heightPercent;

}
