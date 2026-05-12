package com.think_different.think_different.couple.repository;

import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoupleMemberRepository extends JpaRepository<CoupleMember, Long> {

    boolean existsByMember(Member member);
}
