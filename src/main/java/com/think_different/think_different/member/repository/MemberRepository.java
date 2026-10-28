package com.think_different.think_different.member.repository;

import com.think_different.think_different.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    // 아이디 찾기
    Optional<Member> findByEmail(String email);

    // 비밀번호 찾기 (아이디 + 이메일 일치)
    Optional<Member> findByLoginIdAndEmail(String loginId, String email);

    // 아이디 중복 체크
    boolean existsByLoginId(String loginId);

    // 이메일 중복 체크
    boolean existsByEmail(String email);
}
