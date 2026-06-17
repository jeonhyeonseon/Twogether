package com.think_different.think_different.expense.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.expense.dto.ExpenseCreateRequestDto;
import com.think_different.think_different.expense.dto.ExpenseResponseDto;
import com.think_different.think_different.expense.dto.ExpenseUpdateRequestDto;
import com.think_different.think_different.expense.service.ExpenseService;
import com.think_different.think_different.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        List<ExpenseResponseDto> expenses = expenseService.getMonthlyExpense(member, year, month, category);

        model.addAttribute("member", member);
        model.addAttribute("expenseResponseDto", expenses);
        model.addAttribute("selectedCategory", category);

        return "expense/expense";
    }

    @PostMapping
    public String createExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                ExpenseCreateRequestDto createRequestDto) {

        Member member = customUserDetails.getMember();

        expenseService.createExpense(member, createRequestDto);

        return "redirect:/expense";
    }

    @PostMapping("/{expenseId}/edit")
    public String updateExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @PathVariable Long expenseId,
                                ExpenseUpdateRequestDto expenseUpdateRequestDto) {

        Member member = customUserDetails.getMember();

        expenseService.updateExpense(member, expenseId, expenseUpdateRequestDto);

        return "redirect:/expense";
    }

    @PostMapping("/{expenseId}/delete")
    public String deleteExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @PathVariable Long expenseId) {

        Member member = customUserDetails.getMember();

        expenseService.deleteExpense(member, expenseId);

        return "redirect:/expense";
    }
}
