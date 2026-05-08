package com.think_different.think_different.transaction.repository;

import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.statistics.dto.CategoryExpenseDto;
import com.think_different.think_different.statistics.dto.MonthlyExpenseDto;
import com.think_different.think_different.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByMemberAndTransactionDateBetween(Member member, LocalDate start, LocalDate end);

    @Query("""
        select distinct function('date_format', t.transactionDate, '%Y-%m')
        from Transaction t
        where t.member = :member
        order by function('date_format', t.transactionDate, '%Y-%m')
    
        """)
    List<String> findDistinctYearMonthByMember(@Param("member") Member member);

    // 총 수입
    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.member = :member
        and t.transactionType = com.think_different.think_different.transaction.domain.TransactionType.INCOME
        and t.transactionDate between :startDate and :endDate
        """)
    Long sumIncomeByMemberAndMonth(@Param("member") Member member,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    // 총 지출
    @Query("""
        select coalesce(sum(t.amount), 0)
        from Transaction t
        where t.member = :member
        and t.transactionType = com.think_different.think_different.transaction.domain.TransactionType.EXPENSE
        """)
    Long sumExpenseByMemberAndMonth(@Param("member") Member member,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    // 카테고리별
    @Query("""
        select new com.think_different.think_different.statistics.dto.CategoryExpenseDto(
                t.transactionCategory,
                sum(t.amount)
                )
        from Transaction t
        where t.member = :member
            and t.transactionType = com.think_different.think_different.transaction.domain.TransactionType.EXPENSE
            and t.transactionDate between :startDate and :endDate
        group by t.transactionCategory
        """)
    List<CategoryExpenseDto> findCategoryExpenses(@Param("member") Member member,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // 월별
    @Query("""
        select new com.think_different.think_different.statistics.dto.MonthlyExpenseDto(
                year(t.transactionDate),
                month(t.transactionDate),
                sum(t.amount)
                )
        from Transaction t
        where t.member = :member
        and t.transactionType = com.think_different.think_different.transaction.domain.TransactionType.EXPENSE
        group by year(t.transactionDate), month(t.transactionDate)
        order by year(t.transactionDate), month(t.transactionDate)
        """)
    List<MonthlyExpenseDto> findMonthlyExpenses(@Param("member") Member member);
}
