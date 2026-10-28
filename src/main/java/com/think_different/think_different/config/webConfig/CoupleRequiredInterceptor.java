package com.think_different.think_different.config.webConfig;

import com.think_different.think_different.config.webSecurity.CustomUserDetails;
import com.think_different.think_different.couple.servicce.CoupleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

// 커플 공유 화면(일정/지출/기록/버킷리스트 등)은 커플이 연결된 회원만 접근 가능
// 상대가 연결을 해제/탈퇴한 뒤 열어둔 화면에서 요청하면 500 대신 메인(커플 연결 화면)으로 보냄
@Component
@RequiredArgsConstructor
public class CoupleRequiredInterceptor implements HandlerInterceptor {

    private static final String NOT_CONNECTED_MESSAGE = "커플 연결이 해제되었습니다.";

    private final CoupleService coupleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return true; // 비로그인 요청은 Spring Security 가 처리
        }

        if (coupleService.isConnected(customUserDetails.getMember())) {
            return true;
        }

        // 화면 요청은 메인으로, fetch 등 API 요청은 409 + 메시지
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.TEXT_HTML_VALUE)) {
            response.sendRedirect(request.getContextPath() + "/main");
        } else {
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(NOT_CONNECTED_MESSAGE);
        }
        return false;
    }
}
