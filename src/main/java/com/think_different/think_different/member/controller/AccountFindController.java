package com.think_different.think_different.member.controller;

import com.think_different.think_different.member.dto.FindIdRequestDto;
import com.think_different.think_different.member.dto.FindPasswordRequestDto;
import com.think_different.think_different.member.dto.PasswordResetRequestDto;
import com.think_different.think_different.member.service.EmailVerificationService;
import com.think_different.think_different.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

// 아이디 찾기 / 비밀번호 찾기(재설정)
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class AccountFindController {

    // 이메일 인증 완료 후 비밀번호 재설정 가능한 회원/만료 시각 (세션 보관)
    private static final String RESET_MEMBER_ID = "PASSWORD_RESET_MEMBER_ID";
    private static final String RESET_EXPIRES_AT = "PASSWORD_RESET_EXPIRES_AT";
    private static final long RESET_EXPIRE_MINUTES = 10;

    private final MemberService memberService;
    private final EmailVerificationService emailVerificationService;

    // 아이디 찾기 화면
    @GetMapping("/find-id")
    public String showFindIdForm(Model model) {
        model.addAttribute("findIdRequestDto", new FindIdRequestDto());
        return "members/find-id";
    }

    // 아이디 찾기 인증코드 발송
    @ResponseBody
    @PostMapping("/find-id/send-code")
    public ResponseEntity<String> sendFindIdCode(@RequestParam String email) {
        try {
            emailVerificationService.sendFindIdCode(email);
            return ResponseEntity.ok("인증코드가 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 아이디 찾기 (인증코드 확인 후 아이디 표시)
    @PostMapping("/find-id")
    public String findId(@Valid @ModelAttribute("findIdRequestDto") FindIdRequestDto findIdRequestDto,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "members/find-id";
        }

        try {
            emailVerificationService.verifyFindCode(findIdRequestDto.getEmail(), findIdRequestDto.getCode());
            model.addAttribute("loginId", memberService.findLoginIdByEmail(findIdRequestDto.getEmail()));
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("code", "invalid", e.getMessage());
            return "members/find-id";
        }

        return "members/find-id-result";
    }

    // 비밀번호 찾기 화면
    @GetMapping("/find-password")
    public String showFindPasswordForm(Model model) {
        model.addAttribute("findPasswordRequestDto", new FindPasswordRequestDto());
        return "members/find-password";
    }

    // 비밀번호 찾기 인증코드 발송
    @ResponseBody
    @PostMapping("/find-password/send-code")
    public ResponseEntity<String> sendFindPasswordCode(@RequestParam String loginId, @RequestParam String email) {
        try {
            emailVerificationService.sendFindPasswordCode(loginId, email);
            return ResponseEntity.ok("인증코드가 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 비밀번호 찾기 (인증코드 확인 후 재설정 화면으로 이동)
    @PostMapping("/find-password")
    public String findPassword(@Valid @ModelAttribute("findPasswordRequestDto") FindPasswordRequestDto findPasswordRequestDto,
                               BindingResult bindingResult,
                               HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "members/find-password";
        }

        try {
            Long memberId = memberService.findMemberIdForPasswordReset(
                    findPasswordRequestDto.getLoginId(), findPasswordRequestDto.getEmail());
            emailVerificationService.verifyFindCode(findPasswordRequestDto.getEmail(), findPasswordRequestDto.getCode());

            session.setAttribute(RESET_MEMBER_ID, memberId);
            session.setAttribute(RESET_EXPIRES_AT, LocalDateTime.now().plusMinutes(RESET_EXPIRE_MINUTES));
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("code", "invalid", e.getMessage());
            return "members/find-password";
        }

        return "redirect:/members/reset-password";
    }

    // 비밀번호 재설정 화면 (이메일 인증을 완료한 세션만 접근 가능)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (getResetMemberId(session) == null) {
            redirectAttributes.addFlashAttribute("findError", "인증 시간이 만료되었습니다. 다시 인증해주세요.");
            return "redirect:/members/find-password";
        }

        model.addAttribute("passwordResetRequestDto", new PasswordResetRequestDto());
        return "members/reset-password";
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("passwordResetRequestDto") PasswordResetRequestDto passwordResetRequestDto,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Long memberId = getResetMemberId(session);
        if (memberId == null) {
            redirectAttributes.addFlashAttribute("findError", "인증 시간이 만료되었습니다. 다시 인증해주세요.");
            return "redirect:/members/find-password";
        }

        if (StringUtils.hasText(passwordResetRequestDto.getNewPassword())
                && StringUtils.hasText(passwordResetRequestDto.getNewPasswordConfirm())
                && !passwordResetRequestDto.getNewPassword().equals(passwordResetRequestDto.getNewPasswordConfirm())) {
            bindingResult.rejectValue("newPasswordConfirm", "mismatch", "새 비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "members/reset-password";
        }

        memberService.resetPassword(memberId, passwordResetRequestDto.getNewPassword());

        // 재설정은 1회만 가능
        session.removeAttribute(RESET_MEMBER_ID);
        session.removeAttribute(RESET_EXPIRES_AT);

        return "redirect:/members/login";
    }

    // 세션의 재설정 대상 회원 id (만료 시 제거 후 null)
    private Long getResetMemberId(HttpSession session) {
        Long memberId = (Long) session.getAttribute(RESET_MEMBER_ID);
        LocalDateTime expiresAt = (LocalDateTime) session.getAttribute(RESET_EXPIRES_AT);

        if (memberId == null || expiresAt == null || LocalDateTime.now().isAfter(expiresAt)) {
            session.removeAttribute(RESET_MEMBER_ID);
            session.removeAttribute(RESET_EXPIRES_AT);
            return null;
        }
        return memberId;
    }
}
