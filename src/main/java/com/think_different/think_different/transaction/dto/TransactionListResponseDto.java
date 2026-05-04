package com.think_different.think_different.transaction.dto;

import com.think_different.think_different.transaction.domain.Transaction;
import com.think_different.think_different.transaction.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListResponseDto {

    private List<TransactionResponseDto> transactionList;
    private Long totalAmount;
    private YearMonth yearMonth;

    public static TransactionListResponseDto fromTransaction(List<Transaction> transactionList, long totalAmount, YearMonth yearMonth) {

        List<TransactionResponseDto> responseDtoList = transactionList.stream()
                                                    .map(TransactionResponseDto::fromExpense).toList();

        return new TransactionListResponseDto(
                responseDtoList,
                totalAmount,
                yearMonth
        );
    }
}
