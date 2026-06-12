package com.think_different.think_different.calendar.controller;

import com.think_different.think_different.calendar.dto.CalendarRequestDto;
import com.think_different.think_different.calendar.dto.CalendarResponseDto;
import com.think_different.think_different.calendar.service.CalendarService;
import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    public String showCalendar(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               Model model) {

        Member member = customUserDetails.getMember();

        YearMonth currentMonth = YearMonth.now();

        List<CalendarResponseDto> calendarResponseDto = calendarService.getMonthlySchedules(member, currentMonth);

        model.addAttribute("member", member);
        model.addAttribute("calendarResponseDto", calendarResponseDto);

        return "couple/calendar";
    }

    @GetMapping("/daily")
    public List<CalendarResponseDto> showDailySchedule(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @RequestParam LocalDate scheduleDate) {

        Member member = customUserDetails.getMember();

        return calendarService.showDailySchedule(member, scheduleDate);
    }

    @PostMapping
    public String createSchedule(@ModelAttribute CalendarRequestDto calendarRequestDto,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        calendarService.registerSchedule(member, calendarRequestDto);

        return "redirect:/calendar";
    }

    @PostMapping("/{calendarId}/edit")
    public String updateSchedule(@PathVariable Long calendarId,
                                 @ModelAttribute CalendarRequestDto calendarRequestDto,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        calendarService.updateSchedule(member, calendarId, calendarRequestDto);

        return "redirect:/calendar";
    }

    @PostMapping("/{calendarId}/delete")
    public String deleteShcedule(@PathVariable Long calendarId,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = customUserDetails.getMember();

        calendarService.deleteSchedule(member, calendarId);

        return "redirect:/calendar";
    }
}
