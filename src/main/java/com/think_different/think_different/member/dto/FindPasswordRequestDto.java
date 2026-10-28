package com.think_different.think_different.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindPasswordRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId; // 로그인 아이디

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email; // 가입 이메일

    @NotBlank(message = "인증코드를 입력해주세요.")
    private String code; // 이메일 인증코드
}
