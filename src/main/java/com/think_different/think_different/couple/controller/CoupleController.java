package com.think_different.think_different.couple.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.couple.servicce.CoupleService;
import com.think_different.think_different.dashboard.dto.DashboardResponseDto;
import com.think_different.think_different.dashboard.service.DashboardService;
import com.think_different.think_different.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/couple")
@RequiredArgsConstructor
public class CoupleController {

    private final CoupleService coupleService;
    private final DashboardService dashboardService;

    @GetMapping
    public String status(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                         Model model) {

        Member member = customUserDetails.getMember();

        if (coupleService.isConnected(member)) {
            DashboardResponseDto dashboardResponseDto = dashboardService.getDashboard(member);

            model.addAttribute("member", member);
            model.addAttribute("dashboard", dashboardResponseDto);

            return "couple/dashboard";
        }

        return "redirect:/main";
    }

    @GetMapping("/invite")
    public String invitePage(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              Model model) {

        Member member = customUserDetails.getMember();

        String inviteCode = coupleService.getUsableInviteCode(member);

        model.addAttribute("inviteCode", inviteCode);

        return "couple/invite";
    }

    @GetMapping("/connect")
    public String connectPage() {

        return "couple/connect";
    }

    @PostMapping("/invite-code")
    public String createInviteCode(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        coupleService.createInviteCode(member);

        return "redirect:/couple/invite";
    }

    @PostMapping("/connect")
    public String connectCouple(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @RequestParam String code) {

        Member member = customUserDetails.getMember();

        coupleService.connectCouple(member, code);

        return "redirect:/couple";
    }
}
