package com.think_different.think_different.couple.repository;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Object findByMember(Member member);
}
