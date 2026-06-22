package com.think_different.think_different.record.entity;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_date_record")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = false)
    private Couple couple;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Member createdBy; // 작성자

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate dateRecordDate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime createdAt;

    public static DateRecord create(Couple couple, Member createdBy, String title, LocalDate dateRecordDate, String memo) {
        DateRecord dateRecord = new DateRecord();

        dateRecord.couple = couple;
        dateRecord.createdBy = createdBy;
        dateRecord.title = title;
        dateRecord.dateRecordDate = dateRecordDate;
        dateRecord.memo = memo;
        dateRecord.createdAt = LocalDateTime.now();

        return dateRecord;
    }

    public void update(String title, LocalDate dateRecordDate, String memo) {
        this.title = title;
        this.dateRecordDate = dateRecordDate;
        this.memo = memo;
    }
}
