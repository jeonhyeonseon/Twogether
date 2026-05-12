package com.think_different.think_different.couple.domain;

import lombok.Getter;

@Getter
public enum CoupleStatus {

    CONNECTED("연결"),
    DISCONNECTED("연결 해제");

    private final String displayName;

    CoupleStatus(String displayName) {
        this.displayName = displayName;
    }
}
