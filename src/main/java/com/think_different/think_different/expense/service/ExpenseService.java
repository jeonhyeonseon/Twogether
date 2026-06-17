package com.think_different.think_different.expense.service;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.expense.domain.Expense;
import com.think_different.think_different.expense.domain.ExpenseCategory;
import com.think_different.think_different.expense.dto.ExpenseCreateRequestDto;
import com.think_different.think_different.expense.dto.ExpenseResponseDto;
import com.think_different.think_different.expense.dto.ExpenseUpdateRequestDto;
import com.think_different.think_different.expense.repository.ExpenseRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CoupleMemberRepository coupleMemberRepository;

    public List<ExpenseResponseDto> getMonthlyExpense(Member member, Integer year, Integer month, String category) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Couple couple = coupleMember.getCouple();

        YearMonth yearMonth;

        if (year == null || month == null) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.of(year, month);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Expense> expenses;

        if (category == null || category.isBlank() || category.equals("ALL")) {
            expenses = expenseRepository.findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(
                    couple,
                    startDate,
                    endDate
            );
        } else {
            ExpenseCategory expenseCategory = ExpenseCategory.valueOf(category);

            expenses = expenseRepository.findByCoupleAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(
                    couple,
                    expenseCategory,
                    startDate,
                    endDate
            );
        }

        return expenses.stream().map(ExpenseResponseDto::fromExpense).toList();
    }

    public void createExpense(Member member, ExpenseCreateRequestDto createRequestDto) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Couple couple = coupleMember.getCouple();

        Expense expense = Expense.builder()
                .couple(couple)
                .paidBy(member)
                .expenseDate(createRequestDto.getExpenseDate())
                .content(createRequestDto.getContent())
                .category(createRequestDto.getCategory())
                .amount(createRequestDto.getAmount())
                .memo(createRequestDto.getMemo())
                .createdAt(LocalDateTime.now())
                .build();

        expenseRepository.save(expense);
    }

    public void updateExpense(Member member, Long expenseId, ExpenseUpdateRequestDto expenseUpdateRequestDto) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new IllegalArgumentException("비용 정보가 없습니다."));

        if (!expense.getCouple().getId().equals(coupleMember.getCouple().getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        expense.updateExpense(expenseUpdateRequestDto);
    }

    public void deleteExpense(Member member, Long expenseId) {
        
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("비용 정보가 없습니다."));

        if (!expense.getCouple().getId().equals(coupleMember.getCouple().getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        expenseRepository.delete(expense);
    }
}
