package com.think_different.think_different.record.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_date_record_image")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRecordImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "date_record_id")
    private DateRecord dateRecord;

    @Column(nullable = false)
    private String imageUrl;

}
