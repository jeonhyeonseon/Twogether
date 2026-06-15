package com.think_different.think_different.expense.domain;

import lombok.Getter;

@Getter
public enum ExpenseCategory {

    DATE("데이트"),
    FOOD("식비"),
    CAFE("카페"),
    GIFT("선물"),
    TRAVEL("여행"),
    ETC("기타");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
