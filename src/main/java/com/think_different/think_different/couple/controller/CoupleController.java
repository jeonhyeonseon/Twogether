package com.think_different.think_different.couple.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.couple.servicce.CoupleService;
import com.think_different.think_different.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/couple")
@RequiredArgsConstructor
public class CoupleController {

    private final CoupleService coupleService;

    @GetMapping
    public String status(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        boolean connected = coupleService.isConnected(member);

        if (connected) {
            return "couple/dashboard";
        }

        return "couple/connect";
    }

    @GetMapping("/connect")
    public String connectPage() {

        return "couple/connect";
    }

    @PostMapping("/invite-code")
    public String createInviteCode(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        coupleService.createInviteCode(member);

        return "redirect:/couple/connect";
    }
}
