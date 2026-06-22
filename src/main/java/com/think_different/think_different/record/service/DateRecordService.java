package com.think_different.think_different.record.service;

import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.record.dto.DateRecordCreateRequestDto;
import com.think_different.think_different.record.entity.DateRecord;
import com.think_different.think_different.record.repository.DateRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DateRecordService {

    private final CoupleMemberRepository coupleMemberRepository;
    private final DateRecordRepository dateRecordRepository;

    public Long createDateRecord(DateRecordCreateRequestDto dateRecordCreateRequestDto, Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        DateRecord dateRecord = DateRecord.create(
                couple,
                member,
                dateRecordCreateRequestDto.getTitle(),
                dateRecordCreateRequestDto.getDateRecordDate(),
                dateRecordCreateRequestDto.getMemo()
        );

        DateRecord savedDateRecord =
                dateRecordRepository.save(dateRecord);

        return savedDateRecord.getId();
    }
}
