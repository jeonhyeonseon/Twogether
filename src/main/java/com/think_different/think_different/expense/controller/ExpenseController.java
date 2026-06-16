package com.think_different.think_different.expense.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.expense.dto.ExpenseResponseDto;
import com.think_different.think_different.expense.service.ExpenseService;
import com.think_different.think_different.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public String showExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(required = false) Integer month,
                              @RequestParam(required = false, defaultValue = "ALL") String category,
                              Model model) {

        Member member = customUserDetails.getMember();

        List<ExpenseResponseDto> expenseResponseDto = expenseService.getMonthlyExpense(member, year, month, category);

        model.addAttribute("expenseResponseDto", expenseResponseDto);
        model.addAttribute("selectedCategory", category);

        return "expense/expense";
    }
}
