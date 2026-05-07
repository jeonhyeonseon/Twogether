package com.think_different.think_different.statistics.service;

import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.statistics.dto.CategoryExpenseDto;
import com.think_different.think_different.statistics.dto.MonthlyExpenseDto;
import com.think_different.think_different.statistics.dto.StatisticsResponseDto;
import com.think_different.think_different.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticsService {

    private final TransactionRepository transactionRepository;

    public StatisticsResponseDto getStatistics(Member member) {

        Long totalIncome = transactionRepository.sumIncomeByMember(member);

        Long totalExpense = transactionRepository.sumExpenseByMember(member);

        if (totalIncome == null) {
            totalIncome = 0L;
        }

        if (totalExpense == null) {
            totalExpense = 0L;
        }

        // 잔액
        Long balance = totalIncome - totalExpense;

        List<CategoryExpenseDto> categoryExpense = transactionRepository.findCategoryExpenses(member);

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
