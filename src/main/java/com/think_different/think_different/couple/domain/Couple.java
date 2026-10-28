package com.think_different.think_different.couple.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_couple")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private CoupleStatus coupleStatus;

    private LocalDateTime disconnectedAt; // 연결 해제 시각

    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // 커플 연결 해제 (공유 데이터는 삭제하지 않고 상태만 변경)
    public void disconnect() {
        this.coupleStatus = CoupleStatus.DISCONNECTED;
        this.disconnectedAt = LocalDateTime.now();
    }
}
