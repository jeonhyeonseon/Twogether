package com.think_different.think_different.member.dto;

import com.think_different.think_different.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoResponseDto {

    private String name; // 이름
    private String loginId; // 로그인 아이디
    private String email; // 이메일
    private String phone; // 전화번호
    private LocalDateTime createdAt; // 가입일
    private String profileImageUrl; // 화면 표시용 프로필 사진 (없으면 기본 이미지)
    private boolean customProfileImage; // 사용자가 직접 설정한 사진인지

    public static MemberInfoResponseDto from(Member member) {
        return MemberInfoResponseDto.builder()
                .profileImageUrl(member.getDisplayProfileImageUrl())
                .customProfileImage(!Member.DEFAULT_PROFILE_IMAGE_URL.equals(member.getDisplayProfileImageUrl()))
                .name(member.getName())
                .loginId(member.getLoginId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
