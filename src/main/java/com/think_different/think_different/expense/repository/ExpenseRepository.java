package com.think_different.think_different.expense.repository;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.expense.domain.Expense;
import com.think_different.think_different.expense.domain.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(Couple couple, LocalDate startDate, LocalDate endDate);

    List<Expense> findByCoupleAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(Couple couple, ExpenseCategory expenseCategory, LocalDate startDate, LocalDate endDate);

    Optional<Expense> findByIdAndCoupleId(Long expenseId, Long id);

    @Query("select min(e.expenseDate) from Expense e where e.couple = :couple")
    Optional<LocalDate> findMinExpenseDateByCouple(@Param("couple") Couple couple);

    @Query("select max(e.expenseDate) from Expense e where e.couple = :couple")
    Optional<LocalDate> findMaxExpenseDateByCouple(@Param("couple") Couple couple);
}
