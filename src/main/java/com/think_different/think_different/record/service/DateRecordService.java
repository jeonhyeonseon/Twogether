package com.think_different.think_different.record.service;

import com.think_different.think_different.common.file.FileUploadService;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.record.dto.DateRecordCreateRequestDto;
import com.think_different.think_different.record.dto.DateRecordDetailResponseDto;
import com.think_different.think_different.record.dto.DateRecordImageResponseDto;
import com.think_different.think_different.record.dto.DateRecordUpdateRequestDto;
import com.think_different.think_different.record.entity.DateRecord;
import com.think_different.think_different.record.entity.DateRecordImage;
import com.think_different.think_different.record.repository.DateRecordImageRepository;
import com.think_different.think_different.record.repository.DateRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DateRecordService {

    private final CoupleMemberRepository coupleMemberRepository;
    private final DateRecordRepository dateRecordRepository;
    private final DateRecordImageRepository dateRecordImageRepository;
    private final FileUploadService fileUploadService;

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

        DateRecord savedDateRecord = dateRecordRepository.save(dateRecord);

        if (dateRecordCreateRequestDto.getImages() != null) {

            for (MultipartFile image : dateRecordCreateRequestDto.getImages()) {

                if (image.isEmpty()) {
                    continue;
                }

                String imageUrl = fileUploadService.upload(image,"record");

                DateRecordImage dateRecordImage = DateRecordImage.builder()
                                .dateRecord(savedDateRecord)
                                .imageUrl(imageUrl)
                                .build();

                dateRecordImageRepository.save(dateRecordImage);
            }
        }

        return savedDateRecord.getId();
    }

    public DateRecordDetailResponseDto detailDateRecord(Long recordId, Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        DateRecord dateRecord = dateRecordRepository.findByIdAndCoupleId(recordId, couple.getId()).orElseThrow(() -> new IllegalArgumentException("데이트 기록을 찾을 수 없습니다."));

        DateRecordDetailResponseDto detailResponseDto =
                new DateRecordDetailResponseDto();

        detailResponseDto.setId(dateRecord.getId());
        detailResponseDto.setTitle(dateRecord.getTitle());
        detailResponseDto.setDateRecordDate(dateRecord.getDateRecordDate());
        detailResponseDto.setMemo(dateRecord.getMemo());

        List<DateRecordImageResponseDto> images = dateRecordImageRepository.findByDateRecordId(dateRecord.getId())
                        .stream()
                        .map(image -> {
                            DateRecordImageResponseDto imageResponseDto =
                                    new DateRecordImageResponseDto();

                            imageResponseDto.setId(image.getId());
                            imageResponseDto.setImageUrl(image.getImageUrl());

                            return imageResponseDto;
                        })
                        .toList();

        detailResponseDto.setImages(images);

        return detailResponseDto;
    }

    public void updateDateRecord(Long recordId, DateRecordUpdateRequestDto updateRequestDto, Member member) {
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        DateRecord dateRecord = dateRecordRepository.findByIdAndCoupleId(recordId, couple.getId()).orElseThrow(() -> new IllegalArgumentException("데이트 기록을 찾을 수 없습니다."));

        dateRecord.update(
                updateRequestDto.getTitle(),
                updateRequestDto.getDateRecordDate(),
                updateRequestDto.getMemo()
        );

        if (updateRequestDto.getImages() != null &&
                updateRequestDto.getImages().stream().anyMatch(image -> !image.isEmpty())) {

            dateRecordImageRepository.deleteAllByDateRecordId(dateRecord.getId());

            for (MultipartFile image : updateRequestDto.getImages()) {

                if (image.isEmpty()) {
                    continue;
                }

                String imageUrl = fileUploadService.upload(image, "record");

                DateRecordImage dateRecordImage = DateRecordImage.builder()
                        .dateRecord(dateRecord)
                        .imageUrl(imageUrl)
                        .build();

                dateRecordImageRepository.save(dateRecordImage);
            }
        }
    }

    public void deleteDateRecord(Long recordId, Member member) {
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        DateRecord dateRecord = dateRecordRepository.findByIdAndCoupleId(recordId, couple.getId())
                .orElseThrow(() -> new IllegalArgumentException("데이트 기록을 찾을 수 없습니다."));

        dateRecordImageRepository.deleteAllByDateRecordId(dateRecord.getId());

        dateRecordRepository.delete(dateRecord);
    }

    public void addImages(Long recordId, List<MultipartFile> images, Member member) {
        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        DateRecord dateRecord = dateRecordRepository.findByIdAndCoupleId(recordId, couple.getId())
                .orElseThrow(() -> new IllegalArgumentException("데이트 기록을 찾을 수 없습니다."));

        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                continue;
            }

            String imageUrl = fileUploadService.upload(image, "record");

            DateRecordImage dateRecordImage = DateRecordImage.builder()
                    .dateRecord(dateRecord)
                    .imageUrl(imageUrl)
                    .build();

            dateRecordImageRepository.save(dateRecordImage);
        }
    }
}
