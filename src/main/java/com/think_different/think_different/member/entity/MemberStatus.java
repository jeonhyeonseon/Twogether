package com.think_different.think_different.member.entity;

import lombok.Getter;

@Getter
public enum MemberStatus {

    ACTIVE("활성"),
    WITHDRAWN("탈퇴");

    private final String displayName;

    MemberStatus(String displayName) {
        this.displayName = displayName;
    }
}
