package com.think_different.think_different.statistics.dto;

import com.think_different.think_different.transaction.domain.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryExpenseDto {

    private TransactionCategory category;
    private Long amount;

}
