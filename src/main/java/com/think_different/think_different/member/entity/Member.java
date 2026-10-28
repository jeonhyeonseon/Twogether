package com.think_different.think_different.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_member")
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Builder
public class Member {

    public static final String DEFAULT_PROFILE_IMAGE_URL = "/images/default-profile.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB PK = memberId

    private String name; // 이름

    @Column(nullable = false, unique = true)
    private String loginId; // 로그인 시 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String phone; // 전화번호

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 가입일 (기존 회원은 null)

    private String profileImageUrl; // 프로필 사진 (없으면 기본 이미지)

    @Enumerated(EnumType.STRING)
    private MemberStatus status; // 회원 상태 (기존 회원은 null = ACTIVE)

    private LocalDateTime withdrawnAt; // 탈퇴일

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // 마이페이지 정보 수정 (이름, 전화번호만 수정 가능)
    public void updateInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // 비밀번호 변경 (인코딩된 비밀번호를 받음)
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 프로필 사진 변경 (null 이면 기본 이미지로)
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 화면 표시용 프로필 사진 (설정하지 않았으면 기본 이미지)
    public String getDisplayProfileImageUrl() {
        return this.profileImageUrl != null && !this.profileImageUrl.isBlank()
                ? this.profileImageUrl
                : DEFAULT_PROFILE_IMAGE_URL;
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

    // 회원 탈퇴 - 공유 데이터의 FK(작성자 등)는 유지하고 개인정보만 비식별화
    // loginId/email 은 unique 이므로 바꿔두어야 같은 아이디/이메일로 재가입 가능
    public void withdraw(String encodedRandomPassword) {
        this.status = MemberStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
        this.name = "탈퇴한 회원";
        this.loginId = "withdrawn_" + this.id;
        this.email = "withdrawn_" + this.id + "@withdrawn.invalid";
        this.phone = "";
        this.password = encodedRandomPassword;
        this.profileImageUrl = null;
    }
}
