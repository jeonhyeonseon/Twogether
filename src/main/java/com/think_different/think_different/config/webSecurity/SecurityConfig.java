package com.think_different.think_different.config.webSecurity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/scss/**", "/vendor/**", "/images/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApplicationEventPublisher applicationEventPublisher) throws Exception {
        /**
         * - permitAll() : 모두 허용
         * - authenticated() : 인증된 사용자만 허용
         * - anonymous() : 인증하지 않은 사용자만 허용
         * - hasRole(), hasAnyRole() : 특정 권한이 있는 사용자만 허용
         */

        http.authorizeHttpRequests((registry) -> {
            registry
                    .requestMatchers( // permitAll
                            "/",
                            "/members/join",      // 회원가입 화면
                            "/members",           // 회원가입 POST (컨트롤러가 @PostMapping 이면 여기)
                            "/members/login",     // 커스텀 로그인 화면
                            "/login",
                            "/board"
                    ).permitAll()
                    .requestMatchers( // authenticated
                            "/transaction/**",
                            "statistics/**",
                            "/board/**"
                    ).authenticated()
                    .anyRequest().authenticated();
        });

        http.formLogin(configurer -> {
           configurer.loginPage("/members/login")
                   .loginProcessingUrl("/login")
                   .usernameParameter("loginId")
                   .passwordParameter("password")
                   .defaultSuccessUrl("/main", true)
                   .permitAll();
        });

        // 로그아웃 - POST 요청만 가능
        http.logout(configurer -> {
            configurer.logoutUrl("/members/logout")
                    .logoutSuccessUrl("/")
                    .permitAll();
        });

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 로그인 후 리다리렉트 경로 설정

}
