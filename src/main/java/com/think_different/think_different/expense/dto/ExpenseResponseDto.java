package com.think_different.think_different.expense.dto;

import com.think_different.think_different.expense.domain.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDto {

    private Long id;
    private LocalDate expenseDate;
    private String content;
    private String categoryName;
    private String categoryDisplayName;
    private Integer amount;
    private String paidByName;
    private String memo;

    public static ExpenseResponseDto fromExpense(Expense expense) {
        return new ExpenseResponseDto(
                expense.getId(),
                expense.getExpenseDate(),
                expense.getContent(),
                expense.getCategory().name(),
                expense.getCategory().getDisplayName(),
                expense.getAmount(),
                expense.getPaidBy().getName(),
                expense.getMemo()
        );
    }
}
