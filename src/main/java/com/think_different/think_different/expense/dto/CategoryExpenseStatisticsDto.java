package com.think_different.think_different.expense.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryExpenseStatisticsDto {

    private String categoryName;
    private int amount;
    private int heightPercent;

}
