package com.think_different.think_different.record.repository;

import com.think_different.think_different.record.entity.DateRecordExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DateRecordExpenseRepository extends JpaRepository<DateRecordExpense, Long> {

    List<DateRecordExpense> findByDateRecordId(Long id);

    boolean existsByDateRecordIdAndExpenseId(Long recordId, Long expenseId);
}
