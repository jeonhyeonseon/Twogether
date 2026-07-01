package com.think_different.think_different.expense.service;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.expense.domain.Expense;
import com.think_different.think_different.expense.domain.ExpenseCategory;
import com.think_different.think_different.expense.dto.*;
import com.think_different.think_different.expense.repository.ExpenseRepository;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.record.entity.DateRecord;
import com.think_different.think_different.record.entity.DateRecordExpense;
import com.think_different.think_different.record.repository.DateRecordExpenseRepository;
import com.think_different.think_different.record.repository.DateRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final DateRecordRepository dateRecordRepository;
    private final DateRecordExpenseRepository dateRecordExpenseRepository;

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

    public void createExpense(Member member, Long recordId, ExpenseCreateRequestDto createRequestDto) {

        validateExpenseDate(createRequestDto.getExpenseDate());
        validateContent(createRequestDto.getContent());
        validateAmount(createRequestDto.getAmount());

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

        Expense savedExpense = expenseRepository.save(expense);

        if (recordId != null) {
            DateRecord dateRecord = dateRecordRepository.findByIdAndCoupleId(recordId, couple.getId())
                    .orElseThrow(() -> new IllegalArgumentException("데이트 기록을 찾을 수 없습니다."));

            DateRecordExpense dateRecordExpense = DateRecordExpense.create(dateRecord, savedExpense);

            dateRecordExpenseRepository.save(dateRecordExpense);
        }
    }

    public void updateExpense(Member member, Long expenseId, ExpenseUpdateRequestDto expenseUpdateRequestDto) {

        validateExpenseDate(expenseUpdateRequestDto.getExpenseDate());
        validateContent(expenseUpdateRequestDto.getContent());
        validateAmount(expenseUpdateRequestDto.getAmount());

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new IllegalArgumentException("비용 정보가 없습니다."));

        if (!expense.getCouple().getId().equals(coupleMember.getCouple().getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        expense.updateExpense(expenseUpdateRequestDto);
    }

    public void deleteExpense(Member member, Long expenseId) {
        
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new IllegalArgumentException("비용 정보가 없습니다."));

        if (!expense.getCouple().getId().equals(coupleMember.getCouple().getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        expenseRepository.delete(expense);
    }

    public void connectRecord(Member member, Long expenseId, Long recordId) {
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보가 없습니다."));

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new IllegalArgumentException("비용 정보가 없습니다."));

        DateRecord dateRecord = dateRecordRepository.findById(recordId).orElseThrow(() -> new IllegalArgumentException("데이트 기록이 없습니다."));

        if (!expense.getCouple().equals(coupleMember.getCouple())) {
            throw new IllegalArgumentException("연결할 수 없는 비용입니다.");
        }

        if (!dateRecord.getCouple().equals(coupleMember.getCouple())) {
            throw new IllegalArgumentException("연결할 수 없는 데이트 기록입니다.");
        }

        if (dateRecordExpenseRepository.existsByDateRecordIdAndExpenseId(recordId, expenseId)) {
            return;
        }

        DateRecordExpense dateRecordExpense =
                DateRecordExpense.create(dateRecord, expense);

        dateRecordExpenseRepository.save(dateRecordExpense);
    }

    private void validateExpenseDate(LocalDate expenseDate) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (expenseDate == null) {
            throw new IllegalArgumentException("비용 날짜는 필수입니다.");
        }

        if (expenseDate.isAfter(today)) {
            throw new IllegalArgumentException("미래 날짜로는 비용을 등록할 수 없습니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }

        if (content.length() > 30) {
            throw new IllegalArgumentException("내용은 30자 이하로 입력해주세요.");
        }
    }

    private void validateAmount(Integer amount) {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 필수입니다.");
        }

        if (amount < 100) {
            throw new IllegalArgumentException("금액은 100원 이상부터 등록할 수 있습니다.");
        }
    }

    public ExpenseStatisticsResponseDto getExpenseStatistics(Member member,
                                                             Integer year,
                                                             Integer month,
                                                             String category) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 연결 정보가 없습니다."));

        Couple couple = coupleMember.getCouple();

        YearMonth targetMonth;

        if (year == null || month == null) {
            targetMonth = YearMonth.now();
        } else {
            targetMonth = YearMonth.of(year, month);
        }

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        List<Expense> monthlyExpenses;

        if (category == null || category.isBlank() || category.equals("ALL")) {
            monthlyExpenses = expenseRepository.findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(
                            couple,
                            startDate,
                            endDate);
        } else {
            ExpenseCategory expenseCategory = ExpenseCategory.valueOf(category);

            monthlyExpenses = expenseRepository.findByCoupleAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(
                            couple,
                            expenseCategory,
                            startDate,
                            endDate);
        }

        int totalAmount = monthlyExpenses.stream()
                .mapToInt(Expense::getAmount)
                .sum();

        int averageAmount = monthlyExpenses.isEmpty() ? 0 : totalAmount / monthlyExpenses.size();

        List<MonthlyExpenseStatisticsDto> monthlyStatistics =
                java.util.stream.IntStream.rangeClosed(0, 5)
                        .mapToObj(i -> targetMonth.minusMonths(5 - i))
                        .map(yearMonth -> {
                            LocalDate monthStartDate = yearMonth.atDay(1);
                            LocalDate monthEndDate = yearMonth.atEndOfMonth();

                            List<Expense> expenses;

                            if (category == null || category.isBlank() || category.equals("ALL")) {
                                expenses = expenseRepository.findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(
                                                couple,
                                                monthStartDate,
                                                monthEndDate
                                        );
                            } else {
                                ExpenseCategory expenseCategory = ExpenseCategory.valueOf(category);

                                expenses = expenseRepository.findByCoupleAndCategoryAndExpenseDateBetweenOrderByExpenseDateDesc(
                                                couple,
                                                expenseCategory,
                                                monthStartDate,
                                                monthEndDate
                                        );
                            }

                            int amount = expenses.stream()
                                    .mapToInt(Expense::getAmount)
                                    .sum();

                            return MonthlyExpenseStatisticsDto.builder()
                                    .monthLabel(yearMonth.getMonthValue() + "월")
                                    .amount(amount)
                                    .heightPercent(0)
                                    .build();
                        })
                        .toList();

        int maxMonthlyAmount = monthlyStatistics.stream()
                .mapToInt(MonthlyExpenseStatisticsDto::getAmount)
                .max()
                .orElse(0);

        monthlyStatistics = monthlyStatistics.stream()
                .map(dto -> MonthlyExpenseStatisticsDto.builder()
                        .monthLabel(dto.getMonthLabel())
                        .amount(dto.getAmount())
                        .heightPercent(maxMonthlyAmount == 0 ? 0 : Math.max((dto.getAmount() * 100) / maxMonthlyAmount, 8))
                        .build())
                .toList();

        List<Expense> categoryBaseExpenses = expenseRepository.findByCoupleAndExpenseDateBetweenOrderByExpenseDateDesc(
                        couple,
                        startDate,
                        endDate);

        List<CategoryExpenseStatisticsDto> categoryStatistics =
                java.util.Arrays.stream(ExpenseCategory.values())
                        .map(expenseCategory -> {
                            int amount = categoryBaseExpenses.stream()
                                    .filter(expense -> expense.getCategory() == expenseCategory)
                                    .mapToInt(Expense::getAmount)
                                    .sum();

                            return CategoryExpenseStatisticsDto.builder()
                                    .categoryName(expenseCategory.getDisplayName())
                                    .amount(amount)
                                    .heightPercent(0)
                                    .build();
                        })
                        .toList();

        int maxCategoryAmount = categoryStatistics.stream()
                .mapToInt(CategoryExpenseStatisticsDto::getAmount)
                .max()
                .orElse(0);

        categoryStatistics = categoryStatistics.stream()
                .map(dto -> CategoryExpenseStatisticsDto.builder()
                        .categoryName(dto.getCategoryName())
                        .amount(dto.getAmount())
                        .heightPercent(maxCategoryAmount == 0 ? 0 : Math.max((dto.getAmount() * 100) / maxCategoryAmount, 8))
                        .build())
                .toList();

        return ExpenseStatisticsResponseDto.builder()
                .year(targetMonth.getYear())
                .month(targetMonth.getMonthValue())
                .totalAmount(totalAmount)
                .averageAmount(averageAmount)
                .monthlyStatistics(monthlyStatistics)
                .categoryStatistics(categoryStatistics)
                .build();
    }
}