package com.think_different.think_different.couple.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 마이페이지 커플 연결 정보
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoupleConnectionResponseDto {

    private String partnerName; // 상대방 이름
    private LocalDateTime connectedAt; // 연결 날짜

}
