package com.think_different.think_different.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_board")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title; // 제목

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory boardCategory;

    private String contents; // 내용

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt; // 생성시간
    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정시간

    public void updateBoard(String title, String contents) {
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 비어있을 수 없습니다.");
        }
        this.title = title;
        this.contents = contents;
    }
}
