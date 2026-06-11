package com.think_different.think_different.calendar.entity;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_calendar")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = false)
    private Couple couple;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Member createdBy;

    @Column(nullable = false)
    private String title;

    private String memo;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDateTime createdAt;

}
