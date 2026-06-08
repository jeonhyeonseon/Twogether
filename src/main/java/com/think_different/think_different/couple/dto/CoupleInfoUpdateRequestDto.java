package com.think_different.think_different.couple.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CoupleInfoUpdateRequestDto {

    private LocalDate startDate;
}
