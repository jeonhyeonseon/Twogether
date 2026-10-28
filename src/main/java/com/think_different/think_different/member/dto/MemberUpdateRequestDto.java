package com.think_different.think_different.member.dto;

import com.think_different.think_different.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

// 아이디/이메일은 수정 불가이므로 필드 자체를 두지 않음
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberUpdateRequestDto {

    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(
            regexp = "^([가-힣]{2,}|[A-Za-z]{2,})$",
            message = "이름은 한글 또는 영어만 입력 가능합니다. (혼용 불가)"
    )
    private String name; // 이름

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호를 010-XXXX-XXXX 형식으로 모두 입력해주세요.")
    private String phone; // 전화번호

    // 비밀번호 변경 (선택) - 새 비밀번호를 입력한 경우에만 변경
    private String currentPassword; // 현재 비밀번호

    @Pattern(
            regexp = "^$|^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword; // 새 비밀번호

    private String newPasswordConfirm; // 새 비밀번호 확인

    // 프로필 사진 (선택) - 새 사진 업로드 또는 기본 이미지로 되돌리기
    private MultipartFile profileImage; // 새 프로필 사진
    private Boolean removeProfileImage; // true 면 기본 이미지로

    public boolean hasProfileImage() {
        return this.profileImage != null && !this.profileImage.isEmpty();
    }

    public boolean isPasswordChangeRequested() {
        return StringUtils.hasText(this.newPassword) || StringUtils.hasText(this.newPasswordConfirm);
    }

    public static MemberUpdateRequestDto from(Member member) {
        return MemberUpdateRequestDto.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .build();
    }
}
