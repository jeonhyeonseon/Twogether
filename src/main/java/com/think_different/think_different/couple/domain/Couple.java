package com.think_different.think_different.couple.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private LocalDateTime stareDate;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private CoupleStatus coupleStatus;
}
