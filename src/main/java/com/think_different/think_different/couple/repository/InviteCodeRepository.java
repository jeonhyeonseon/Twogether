package com.think_different.think_different.couple.repository;

import com.think_different.think_different.couple.domain.InviteCode;
import com.think_different.think_different.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    boolean existsByMemberAndUsedIsFalse(Member member);

    boolean existsByCode(String code);
}
