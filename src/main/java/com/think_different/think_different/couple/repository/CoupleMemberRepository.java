package com.think_different.think_different.couple.repository;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoupleMemberRepository extends JpaRepository<CoupleMember, Long> {

    // 연결 해제된 커플(DISCONNECTED)의 CoupleMember 행은 남아 있으므로, 연결 여부/조회는 반드시 상태 기준으로 함
    @Query("select count(cm) > 0 from CoupleMember cm " +
            "where cm.member = :member " +
            "and cm.couple.coupleStatus = com.think_different.think_different.couple.domain.CoupleStatus.CONNECTED")
    boolean existsConnectedByMember(@Param("member") Member member);

    @Query("select cm from CoupleMember cm join fetch cm.couple c " +
            "where cm.member = :member " +
            "and c.coupleStatus = com.think_different.think_different.couple.domain.CoupleStatus.CONNECTED")
    Optional<CoupleMember> findConnectedByMember(@Param("member") Member member);

    List<CoupleMember> findByCouple(Couple couple);
}
