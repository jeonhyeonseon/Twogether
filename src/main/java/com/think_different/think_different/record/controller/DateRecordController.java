package com.think_different.think_different.record.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.record.dto.DateRecordCreateRequestDto;
import com.think_different.think_different.record.service.DateRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/record")
@RequiredArgsConstructor
public class DateRecordController {

    private final DateRecordService dateRecordService;

    @GetMapping("/create")
    public String showDateRecordFrom() {
        return "record/create";
    }

    @PostMapping("/create")
    public String createDateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   DateRecordCreateRequestDto dateRecordCreateRequestDto) {

        Member member = customUserDetails.getMember();

        Long recordId = dateRecordService.createDateRecord(dateRecordCreateRequestDto, member);

        return "redirect:/record/" + recordId;
    }
}
