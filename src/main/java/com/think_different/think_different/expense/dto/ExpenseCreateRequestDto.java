package com.think_different.think_different.expense.dto;

import com.think_different.think_different.expense.domain.ExpenseCategory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseCreateRequestDto {

    private LocalDate expenseDate;
    private String content;
    private ExpenseCategory category;
    private Integer amount;
    private String memo;

}
