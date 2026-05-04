package com.think_different.think_different.transaction.controller;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.transaction.domain.Transaction;
import com.think_different.think_different.transaction.domain.TransactionCategory;
import com.think_different.think_different.transaction.domain.TransactionType;
import com.think_different.think_different.transaction.dto.TransactionCreateRequestDto;
import com.think_different.think_different.transaction.dto.TransactionListResponseDto;
import com.think_different.think_different.transaction.dto.TransactionUpdateRequestDto;
import com.think_different.think_different.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public String showTransactionList(@RequestParam(required = false) String month,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      Model model) {

        YearMonth current = (month == null || month.isBlank())
                            ? YearMonth.now() : YearMonth.parse(month);

        Member member = customUserDetails.getMember();

        TransactionListResponseDto listResponseDto = transactionService.listTransaction(current, member);

        List<YearMonth> writtenMonth = transactionService.findWrittenMonth(member);

        YearMonth prevMonth = null;
        YearMonth nextMonth = null;

        int indexMonth = writtenMonth.indexOf(current);
        if (indexMonth != -1) {
            if (indexMonth > 0) prevMonth = writtenMonth.get(indexMonth - 1);
            if (indexMonth < writtenMonth.size() - 1) nextMonth = writtenMonth.get(indexMonth + 1);
        }

        model.addAttribute("listResponseDto", listResponseDto);
        model.addAttribute("prevMonth",prevMonth);
        model.addAttribute("nextMonth", nextMonth);

        return "transaction/list";
    }

    @GetMapping("/new")
    public String showCreateTransactionForm(Model model) {

        model.addAttribute("types", TransactionType.values());
        model.addAttribute("categories", TransactionCategory.values());

        return "transaction/create";
    }

    @PostMapping
    public String createTransaction(@ModelAttribute TransactionCreateRequestDto createRequestDto,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        transactionService.createTransaction(createRequestDto, member);

        return "redirect:/transaction";
    }

    @GetMapping("/{id}")
    public String showEditTransactionForm(@PathVariable Long id,
                                      Model model) {

        TransactionUpdateRequestDto updateRequestDto = transactionService.updateTransactionForm(id);

        model.addAttribute("types", TransactionType.values());
        model.addAttribute("categories", TransactionCategory.values());
        model.addAttribute("updateRequestDto", updateRequestDto);

        return "transaction/edit";
    }

    @PostMapping("/{id}/edit")
    public String editTransaction(@PathVariable Long id,
                                  @ModelAttribute TransactionUpdateRequestDto updateRequestDto,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        transactionService.updateTransaction(id, updateRequestDto, member);

        YearMonth yearMonth = YearMonth.from(updateRequestDto.getTransactionDate());

        return "redirect:/transaction?month=" + yearMonth;
    }

    @PostMapping("/{id}/delete")
    public String deleteTransaction(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        transactionService.deleteTransaction(id, member);

        return "redirect:/transaction";
    }
}
