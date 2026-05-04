package com.think_different.think_different.transaction.dto;

import com.think_different.think_different.transaction.domain.Transaction;
import com.think_different.think_different.transaction.domain.TransactionCategory;
import com.think_different.think_different.transaction.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {

    private Long id;
    private String detail;
    private TransactionCategory transactionCategory;
    private TransactionType transactionType;
    private Long amount;
    private LocalDate transactionDate;

    public static TransactionResponseDto fromExpense(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getDetail(),
                transaction.getTransactionCategory(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getTransactionDate()
        );
    }
}
