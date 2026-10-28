package com.think_different.think_different.config.webSecurity;

import com.think_different.think_different.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public String getName() {
        return member.getName();
    }

    // 헤더 프로필 사진 (설정하지 않았으면 기본 이미지)
    public String getProfileImageUrl() {
        return member.getDisplayProfileImageUrl();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override
    // 계정 자체가 만료되었는가?
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // 계정이 잠겨있는가?
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // 비밀번호가 만료되었는가?
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 이 계정 사용 가능한 상태인가? (탈퇴 회원은 로그인 불가)
    public boolean isEnabled() {
        return !member.isWithdrawn();
    }
}
