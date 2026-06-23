package com.think_different.think_different.record.entity;

import com.think_different.think_different.expense.domain.Expense;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_date_record_expense")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DateRecordExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "date_record_id", nullable = false)
    private DateRecord dateRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    public static DateRecordExpense create(DateRecord dateRecord, Expense expense) {
        DateRecordExpense dateRecordExpense = new DateRecordExpense();

        dateRecordExpense.dateRecord = dateRecord;
        dateRecordExpense.expense = expense;

        return dateRecordExpense;
    }
}
