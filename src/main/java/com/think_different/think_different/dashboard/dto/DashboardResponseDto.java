package com.think_different.think_different.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class DashboardResponseDto {

    private String memberName;
    private String partnerName;

    private LocalDateTime startDate;
    private Long dDay;
    private boolean hasStartDate;
}
