package com.think_different.think_different.couple.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class CoupleInfoUpdateRequestDto {

    private String myNickname;

    private String partnerNickname;

    private MultipartFile myProfileImage;

    private MultipartFile partnerProfileImage;

    private LocalDate startDate;
}
