package com.think_different.think_different.main.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.couple.servicce.CoupleService;
import com.think_different.think_different.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final CoupleService coupleService;

    @GetMapping
    public String main(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                       Model model) {

        Member member = customUserDetails.getMember();

        boolean connected = coupleService.isConnected(member);

        if (connected) {
            return "redirect:/couple";
        }

        model.addAttribute("connected", false);
        model.addAttribute("member", member);

        return "main/main";
    }

}
