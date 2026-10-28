package com.think_different.think_different.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawRequestDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password; // 비밀번호 재확인
}
