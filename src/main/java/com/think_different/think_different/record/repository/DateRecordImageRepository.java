package com.think_different.think_different.record.repository;

import com.think_different.think_different.record.entity.DateRecordImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DateRecordImageRepository extends JpaRepository<DateRecordImage, Long> {

    List<DateRecordImage> findByDateRecordId(Long dateRecordId);

    void deleteByIdAndDateRecordId(Long imageId, Long id);
}
