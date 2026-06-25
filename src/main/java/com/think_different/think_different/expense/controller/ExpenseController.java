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
                              @RequestParam(required = false) Long recordId,
                              @RequestParam(required = false) String mode,
                              Model model) {

        Member member = customUserDetails.getMember();

        // 목록용: 선택한 카테고리 기준
        List<ExpenseResponseDto> expenses =
                expenseService.getMonthlyExpense(member, year, month, category);

        // 통계용: 이번 달 전체 기준
        List<ExpenseResponseDto> allExpenses =
                expenseService.getMonthlyExpense(member, year, month, "ALL");

        int totalAmount = allExpenses.stream()
                .mapToInt(ExpenseResponseDto::getAmount)
                .sum();

        int averageAmount = allExpenses.isEmpty()
                ? 0
                : totalAmount / allExpenses.size();

        model.addAttribute("member", member);
        model.addAttribute("recordId", recordId);
        model.addAttribute("mode", mode);

        model.addAttribute("expenseResponseDto", expenses);
        model.addAttribute("selectedCategory", category);

        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("dateCount", 0);
        model.addAttribute("averageAmount", averageAmount);

        return "expense/expense";
    }

    @PostMapping
    public String createExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @RequestParam(required = false) Long recordId,
                                ExpenseCreateRequestDto createRequestDto) {

        Member member = customUserDetails.getMember();

        expenseService.createExpense(member, recordId, createRequestDto);

        if (recordId != null) {
            return "redirect:/record/" + recordId;
        }

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

    @PostMapping("/{expenseId}/connect-record")
    public String connectRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @PathVariable Long expenseId,
                                @RequestParam Long recordId) {

        Member member = customUserDetails.getMember();

        expenseService.connectRecord(member, expenseId, recordId);

        return "redirect:/record/" + recordId;
    }
}
