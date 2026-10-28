package com.think_different.think_different.member.controller;

import com.think_different.think_different.common.file.FileUploadService;
import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.couple.servicce.CoupleService;
import com.think_different.think_different.member.dto.MemberInfoResponseDto;
import com.think_different.think_different.member.dto.MemberUpdateRequestDto;
import com.think_different.think_different.member.dto.WithdrawRequestDto;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 회원 식별은 요청 파라미터가 아닌 로그인 사용자(@AuthenticationPrincipal)로만 처리
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {

    private final MemberService memberService;
    private final CoupleService coupleService;
    private final FileUploadService fileUploadService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // 마이페이지 화면
    @GetMapping
    public String showMypage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        Member member = memberService.getMember(customUserDetails.getMember().getId());

        model.addAttribute("memberUpdateRequestDto", MemberUpdateRequestDto.from(member));
        return renderMypage(member, model);
    }

    // 내 정보 수정 (이름, 전화번호 + 새 비밀번호를 입력한 경우 비밀번호까지 함께 변경)
    @PostMapping
    public String updateMyInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               @Valid @ModelAttribute("memberUpdateRequestDto") MemberUpdateRequestDto memberUpdateRequestDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Long memberId = customUserDetails.getMember().getId();
        boolean passwordChangeRequested = memberUpdateRequestDto.isPasswordChangeRequested();

        if (passwordChangeRequested) {
            if (!StringUtils.hasText(memberUpdateRequestDto.getCurrentPassword())) {
                bindingResult.rejectValue("currentPassword", "required", "현재 비밀번호를 입력해주세요.");
            } else if (!memberService.matchesPassword(memberId, memberUpdateRequestDto.getCurrentPassword())) {
                bindingResult.rejectValue("currentPassword", "mismatch", "현재 비밀번호가 일치하지 않습니다.");
            }

            if (!StringUtils.hasText(memberUpdateRequestDto.getNewPassword())) {
                bindingResult.rejectValue("newPassword", "required", "새 비밀번호를 입력해주세요.");
            } else if (!bindingResult.hasFieldErrors("newPassword")
                    && memberService.matchesPassword(memberId, memberUpdateRequestDto.getNewPassword())) {
                bindingResult.rejectValue("newPassword", "same", "현재 비밀번호와 다른 비밀번호를 입력해주세요.");
            }

            if (!StringUtils.hasText(memberUpdateRequestDto.getNewPasswordConfirm())) {
                bindingResult.rejectValue("newPasswordConfirm", "required", "새 비밀번호 확인을 입력해주세요.");
            } else if (!memberUpdateRequestDto.getNewPasswordConfirm().equals(memberUpdateRequestDto.getNewPassword())) {
                bindingResult.rejectValue("newPasswordConfirm", "mismatch", "새 비밀번호가 일치하지 않습니다.");
            }
        }

        // 프로필 사진 형식/용량 확인
        String profileImageError = fileUploadService.validateImage(memberUpdateRequestDto.getProfileImage());
        if (profileImageError != null) {
            bindingResult.rejectValue("profileImage", "invalid", profileImageError);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", true); // 에러 메시지와 입력값이 보이도록 수정 모드로
            if (passwordChangeRequested) {
                model.addAttribute("passwordFormOpen", true); // 비밀번호 입력칸도 열린 상태로
            }
            return renderMypage(memberService.getMember(memberId), model);
        }

        Member member = memberService.updateMyInfo(memberId, memberUpdateRequestDto);
        refreshAuthentication(member, request, response);

        redirectAttributes.addFlashAttribute("successMessage", "내 정보가 수정되었습니다.");
        return "redirect:/mypage";
    }

    // 커플 연결 해제 (화면에서 확인 창을 거친 뒤 요청)
    @PostMapping("/couple/disconnect")
    public String disconnectCouple(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   RedirectAttributes redirectAttributes) {
        Member member = memberService.getMember(customUserDetails.getMember().getId());

        try {
            coupleService.disconnectCouple(member);
            redirectAttributes.addFlashAttribute("successMessage", "커플 연결이 해제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/mypage";
    }

    // 회원 탈퇴 (비밀번호 재확인 → 커플 연결 해제 → 탈퇴 처리 → 로그아웃)
    @PostMapping("/withdraw")
    public String withdraw(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                           @Valid @ModelAttribute("withdrawRequestDto") WithdrawRequestDto withdrawRequestDto,
                           BindingResult bindingResult,
                           Model model,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        Long memberId = customUserDetails.getMember().getId();

        if (!bindingResult.hasErrors()) {
            try {
                memberService.withdraw(memberId, withdrawRequestDto.getPassword());
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("password", "mismatch", e.getMessage());
            }
        }

        if (bindingResult.hasErrors()) {
            // 탈퇴 입력칸은 정보 수정 박스(수정 모드) 안에 있으므로 수정 모드 + 탈퇴 입력칸을 열린 상태로
            model.addAttribute("editMode", true);
            model.addAttribute("withdrawFormOpen", true);
            model.addAttribute("withdrawError", bindingResult.getFieldError("password").getDefaultMessage());
            Member member = memberService.getMember(memberId);
            model.addAttribute("memberUpdateRequestDto", MemberUpdateRequestDto.from(member));
            return renderMypage(member, model);
        }

        // 세션 무효화 + SecurityContext 제거 (로그아웃)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return "redirect:/";
    }

    // 마이페이지 공통 모델 (조회 정보, 커플 연결 정보)
    private String renderMypage(Member member, Model model) {
        model.addAttribute("memberInfo", MemberInfoResponseDto.from(member));
        model.addAttribute("coupleConnection", coupleService.getConnection(member).orElse(null));
        return "members/mypage";
    }

    // 세션의 로그인 정보(CustomUserDetails)를 변경된 회원 정보로 갱신 - 로그인 유지
    private void refreshAuthentication(Member member, HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetails updatedUserDetails = new CustomUserDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
