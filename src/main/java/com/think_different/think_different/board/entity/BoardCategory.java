package com.think_different.think_different.board.entity;

import lombok.Getter;

@Getter
public enum BoardCategory {

    REVIEW("후기"),
    SUGGESTION("건의사항"),
    QUESTION("질문");

    private final String displayName;

    BoardCategory(String displayName) {
        this.displayName = displayName;
    }
}
