package com.think_different.think_different.record.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.record.dto.DateRecordCreateRequestDto;
import com.think_different.think_different.record.dto.DateRecordDetailResponseDto;
import com.think_different.think_different.record.dto.DateRecordUpdateRequestDto;
import com.think_different.think_different.record.service.DateRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/record")
@RequiredArgsConstructor
public class DateRecordController {

    private final DateRecordService dateRecordService;

    @GetMapping("/create")
    public String showDateRecordFrom(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     Model model) {

        Member member = customUserDetails.getMember();

        model.addAttribute("member", member);

        return "record/create";
    }

    @PostMapping("/create")
    public String createDateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @ModelAttribute DateRecordCreateRequestDto dateRecordCreateRequestDto) {

        Member member = customUserDetails.getMember();

        Long recordId = dateRecordService.createDateRecord(dateRecordCreateRequestDto, member);

        return "redirect:/record/" + recordId;
    }

    @GetMapping("/{recordId}")
    public String detailDateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @PathVariable Long recordId,
                                   Model model) {

        Member member = customUserDetails.getMember();

        DateRecordDetailResponseDto detailResponseDto = dateRecordService.detailDateRecord(recordId, member);

        model.addAttribute("member", member);
        model.addAttribute("record", detailResponseDto);

        return "record/detail";
    }

    @GetMapping("/{recordId}/edit")
    public String showEditDateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @PathVariable Long recordId,
                                     Model model) {

        Member member = customUserDetails.getMember();

        DateRecordDetailResponseDto detailResponseDto = dateRecordService.detailDateRecord(recordId, member);

        model.addAttribute("member", member);
        model.addAttribute("record", detailResponseDto);

        return "record/edit";
    }

    @PostMapping("/{recordId}/edit")
    public String editDateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                 @PathVariable Long recordId,
                                 @ModelAttribute DateRecordUpdateRequestDto updateRequestDto) {

        Member member = customUserDetails.getMember();

        dateRecordService.updateDateRecord(recordId, updateRequestDto, member);

        return "redirect:/record/" + recordId;
    }

    @PostMapping("/{recordId}/delete")
    public String deleteDateRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @PathVariable Long recordId) {

        Member member = customUserDetails.getMember();

        dateRecordService.deleteDateRecord(recordId, member);

        return "redirect:/record";
    }
}
