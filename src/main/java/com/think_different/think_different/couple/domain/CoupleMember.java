package com.think_different.think_different.couple.domain;

import com.think_different.think_different.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_couple_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoupleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String nickname;

    private LocalDateTime joinedAt; // 연결 날짜

    public void updateDisplayInfo(String nickname) {
        this.nickname = nickname;
    }
}
