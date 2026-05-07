package com.think_different.think_different.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsResponseDto {

    private Long totalIncome; // 총 수입
    private Long totalExpense; // 총 지출
    private Long balance; // 잔액

    private List<CategoryExpenseDto> categoryExpenses; // 카테고리별 소비
    private List<MonthlyExpenseDto> monthlyExpenses; // 월별 소비
}
