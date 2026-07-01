package com.think_different.think_different.expense.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@Builder
public class ExpenseStatisticsResponseDto {

    private int year;
    private int month;

    private int totalAmount;
    private int averageAmount;

    private List<MonthlyExpenseStatisticsDto> monthlyStatistics;
    private List<CategoryExpenseStatisticsDto> categoryStatistics;

}
