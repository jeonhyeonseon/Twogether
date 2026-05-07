package com.think_different.think_different.statistics.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.statistics.dto.StatisticsResponseDto;
import com.think_different.think_different.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public String statistics(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                             Model model) {

        Member member = customUserDetails.getMember();

        StatisticsResponseDto statisticsDto = statisticsService.getStatistics(member);

        model.addAttribute("statistics", statisticsDto);

        return "statistics/statistics";
    }
}
