package com.think_different.think_different.bucketlist.service;

import com.think_different.think_different.bucketlist.dto.BucketListRequestDto;
import com.think_different.think_different.bucketlist.dto.BucketListResponseDto;
import com.think_different.think_different.bucketlist.entity.BucketList;
import com.think_different.think_different.bucketlist.repository.BucketListRepository;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BucketListService {

    private final BucketListRepository bucketListRepository;
    private final CoupleMemberRepository coupleMemberRepository;

    public List<BucketListResponseDto> getBucketList(Member member) {

        Couple couple = resolveCouple(member);

        return bucketListRepository.findByCoupleOrderByCreatedAtDesc(couple)
                .stream()
                .map(BucketListResponseDto::fromBucketList)
                .toList();
    }

    public Long createBucketList(Member member, BucketListRequestDto requestDto) {

        Couple couple = resolveCouple(member);

        BucketList bucketList = requestDto.toBucketList(couple, member);

        return bucketListRepository.save(bucketList).getId();
    }

    public void updateBucketList(Member member, Long bucketListId, BucketListRequestDto requestDto) {

        Couple couple = resolveCouple(member);

        BucketList bucketList = findOwnedBucketList(couple, bucketListId, "수정 권한이 없습니다.");

        bucketList.updateInfo(
                requestDto.getTitle(),
                requestDto.getMemo(),
                requestDto.getPlaceType(),
                requestDto.getSeason(),
                requestDto.getPriority()
        );
    }

    public void deleteBucketList(Member member, Long bucketListId) {

        Couple couple = resolveCouple(member);

        BucketList bucketList = findOwnedBucketList(couple, bucketListId, "삭제 권한이 없습니다.");

        bucketListRepository.delete(bucketList);
    }

    public BucketListResponseDto getBucketListItem(Member member, Long bucketListId) {

        Couple couple = resolveCouple(member);

        BucketList bucketList = findOwnedBucketList(couple, bucketListId, "조회 권한이 없습니다.");

        return BucketListResponseDto.fromBucketList(bucketList);
    }

    public void toggleComplete(Member member, Long bucketListId) {

        Couple couple = resolveCouple(member);

        BucketList bucketList = findOwnedBucketList(couple, bucketListId, "수정 권한이 없습니다.");

        bucketList.toggleComplete();
    }

    private Couple resolveCouple(Member member) {
        CoupleMember coupleMember = coupleMemberRepository.findConnectedByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        return coupleMember.getCouple();
    }

    private BucketList findOwnedBucketList(Couple couple, Long bucketListId, String forbiddenMessage) {
        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new IllegalArgumentException("버킷리스트 항목을 찾을 수 없습니다."));

        if (!bucketList.getCouple().getId().equals(couple.getId())) {
            throw new IllegalArgumentException(forbiddenMessage);
        }

        return bucketList;
    }
}
