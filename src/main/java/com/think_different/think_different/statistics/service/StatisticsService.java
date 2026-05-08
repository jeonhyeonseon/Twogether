package com.think_different.think_different.statistics.service;

import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.statistics.dto.CategoryExpenseDto;
import com.think_different.think_different.statistics.dto.MonthlyExpenseDto;
import com.think_different.think_different.statistics.dto.StatisticsResponseDto;
import com.think_different.think_different.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticsService {

    private final TransactionRepository transactionRepository;

    public StatisticsResponseDto getStatistics(Member member, YearMonth yearMonth) {

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Long totalIncome = transactionRepository.sumIncomeByMemberAndMonth(member, startDate, endDate);

        Long totalExpense = transactionRepository.sumExpenseByMemberAndMonth(member, startDate, endDate);

        if (totalIncome == null) {
            totalIncome = 0L;
        }

        if (totalExpense == null) {
            totalExpense = 0L;
        }

        // 잔액
        Long balance = totalIncome - totalExpense;

        List<CategoryExpenseDto> categoryExpense = transactionRepository.findCategoryExpenses(member, startDate, endDate);

        List<MonthlyExpenseDto> monthlyExpense = transactionRepository.findMonthlyExpenses(member);

        return StatisticsResponseDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .categoryExpenses(categoryExpense)
                .monthlyExpenses(monthlyExpense)
                .build();
    }
}
