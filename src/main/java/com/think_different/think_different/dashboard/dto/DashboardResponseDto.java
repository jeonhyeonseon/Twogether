package com.think_different.think_different.dashboard.dto;

import com.think_different.think_different.calendar.dto.CalendarResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DashboardResponseDto {

    private String memberName;
    private String partnerName;

    private String myNickname;
    private String partnerNickname;

    private String myProfileImageUrl;
    private String partnerProfileImageUrl;

    private int monthlyTotalAmount;
    private int monthlyAverageAmount;
    private int monthlyDateCount;

    private LocalDate startDate;
    private Long dDay;
    private boolean hasStartDate;

    private List<CalendarResponseDto> upcomingSchedules;

    public String getDisplayMyName() {
        return myNickname != null && !myNickname.isBlank()
                ? myNickname
                : memberName;
    }

    public String getDisplayPartnerName() {
        return partnerNickname != null && !partnerNickname.isBlank()
                ? partnerNickname
                : partnerName;
    }

    public String getDisplayMyProfileImageUrl() {
        return myProfileImageUrl != null && !myProfileImageUrl.isBlank()
                ? myProfileImageUrl
                : "/images/default-profile.png";
    }

    public String getDisplayPartnerProfileImageUrl() {
        return partnerProfileImageUrl != null && !partnerProfileImageUrl.isBlank()
                ? partnerProfileImageUrl
                : "/images/default-profile.png";
    }
}
