package com.think_different.think_different.transaction.service;

import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.transaction.domain.Transaction;
import com.think_different.think_different.transaction.domain.TransactionType;
import com.think_different.think_different.transaction.dto.TransactionCreateRequestDto;
import com.think_different.think_different.transaction.dto.TransactionListResponseDto;
import com.think_different.think_different.transaction.dto.TransactionUpdateRequestDto;
import com.think_different.think_different.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionListResponseDto listTransaction(YearMonth yearMonth, Member member) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Transaction> transactionList = transactionRepository.findByMemberAndTransactionDateBetween(member, start, end);

        long incomeTotal = 0;
        long expenseTotal = 0;

        for (Transaction t : transactionList) {
            if (t.getTransactionType() == TransactionType.EXPENSE) {
                expenseTotal = expenseTotal + t.getAmount();
            } else {
                incomeTotal = incomeTotal + t.getAmount();
            }
        }

        long totalAmount = incomeTotal - expenseTotal;

        return TransactionListResponseDto.fromTransaction(transactionList, totalAmount, incomeTotal, expenseTotal, yearMonth);
    }

    public List<YearMonth> findWrittenMonth(Member member) {
        return transactionRepository.findDistinctYearMonthByMember(member)
                                .stream()
                                .map(YearMonth::parse)
                                .toList();
    }

    public void createTransaction(TransactionCreateRequestDto createRequestDto, Member member) {

        if (createRequestDto.getTransactionCategory() == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }

        Transaction transaction = createRequestDto.toTransaction(member);

        transactionRepository.save(transaction);
    }

    public TransactionUpdateRequestDto updateTransactionForm(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래입니다."));

        return TransactionUpdateRequestDto.fromTransaction(transaction);
    }

    public void updateTransaction(Long id, TransactionUpdateRequestDto updateRequestDto, Member member) {

        Transaction transaction = transactionRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래입니다."));

        if (!transaction.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("작성자만이 거래를 수정할 수 있습니다.");
        }

        transaction.updateTransaction(updateRequestDto.getDetail(),
                                      updateRequestDto.getTransactionCategory().getType(),
                                      updateRequestDto.getTransactionCategory(),
                                      updateRequestDto.getAmount(),
                                      updateRequestDto.getTransactionDate()
        );
    }

    public void deleteTransaction(Long id, Member member) {

        Transaction transaction = transactionRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래입니다."));

        if (!transaction.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("작성자만이 거래를 삭제할 수 있습니다.");
        }

        transactionRepository.delete(transaction);
    }
}
