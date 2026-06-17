package com.think_different.think_different.expense.dto;

import com.think_different.think_different.expense.domain.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseUpdateRequestDto {

    private LocalDate expenseDate;
    private String content;
    private ExpenseCategory category;
    private Integer amount;
    private String memo;

}
