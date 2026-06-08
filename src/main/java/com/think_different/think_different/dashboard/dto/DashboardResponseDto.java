package com.think_different.think_different.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class DashboardResponseDto {

    private String memberName;
    private String partnerName;

    private LocalDate startDate;
    private Long dDay;
    private boolean hasStartDate;
}
