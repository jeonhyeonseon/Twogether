package com.think_different.think_different.transaction.domain;

import lombok.Getter;

@Getter
public enum TransactionType {

    INCOME("수입"),
    EXPENSE("지출");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }
}
