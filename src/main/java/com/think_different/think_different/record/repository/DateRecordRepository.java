package com.think_different.think_different.record.repository;

import com.think_different.think_different.record.entity.DateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DateRecordRepository extends JpaRepository<DateRecord, Long> {

    Optional<DateRecord> findByIdAndCoupleId(Long recordId, Long id);

    List<DateRecord> findTop3ByCoupleIdOrderByCreatedAtDesc(Long id);

    List<DateRecord> findByCoupleIdOrderByDateRecordDateDescCreatedAtDesc(Long id);
}
