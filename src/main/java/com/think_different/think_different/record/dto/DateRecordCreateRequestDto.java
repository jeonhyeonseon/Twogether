package com.think_different.think_different.record.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class DateRecordCreateRequestDto {

    private String title;
    private LocalDate dateRecordDate;
    private String memo;
    private List<MultipartFile> images;
}
