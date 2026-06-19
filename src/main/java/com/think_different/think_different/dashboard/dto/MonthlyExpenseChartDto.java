package com.think_different.think_different.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyExpenseChartDto {

    private String monthLabel;
    private int amount;
    private int heightPercent;

}
