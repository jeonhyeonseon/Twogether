package com.think_different.think_different.expense.repository;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.expense.domain.Expense;
import com.think_different.think_different.expense.domain.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(Couple couple, LocalDate startDate, LocalDate endDate);

    List<Expense> findByCoupleAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(Couple couple, ExpenseCategory expenseCategory, LocalDate startDate, LocalDate endDate);
}
