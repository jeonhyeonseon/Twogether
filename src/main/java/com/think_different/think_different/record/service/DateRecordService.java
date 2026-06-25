package com.think_different.think_different.record.service;

import com.think_different.think_different.common.file.FileUploadService;
import com.think_different.think_different.couple.domain.Couple;
import com.think_different.think_different.couple.domain.CoupleMember;
import com.think_different.think_different.couple.repository.CoupleMemberRepository;
import com.think_different.think_different.expense.domain.Expense;
import com.think_different.think_different.expense.repository.ExpenseRepository;
import com.think_different.think_different.member.entity.Member;
import com.think_different.think_different.record.dto.*;
import com.think_different.think_different.record.entity.DateRecord;
import com.think_different.think_different.record.entity.DateRecordExpense;
import com.think_different.think_different.record.entity.DateRecordImage;
import com.think_different.think_different.record.repository.DateRecordExpenseRepository;
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
    private final DateRecordExpenseRepository dateRecordExpenseRepository;
    private final ExpenseRepository expenseRepository;

    public List<DateRecordListResponseDto> getDateRecordList(Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        return dateRecordRepository.findByCoupleIdOrderByDateRecordDateDescCreatedAtDesc(couple.getId())
                .stream()
                .map(record -> {
                    DateRecordListResponseDto dto = new DateRecordListResponseDto();

                    dto.setId(record.getId());
                    dto.setTitle(record.getTitle());
                    dto.setDateRecordDate(record.getDateRecordDate());
                    dto.setMemo(record.getMemo());

                    List<DateRecordImage> images =
                            dateRecordImageRepository.findByDateRecordId(record.getId());

                    if (!images.isEmpty()) {
                        dto.setThumbnailImageUrl(images.get(0).getImageUrl());
                    } else {
                        dto.setThumbnailImageUrl("/images/default-record.png");
                    }

                    return dto;
                })
                .toList();
    }

    public List<DateRecordRecentResponseDto> getRecentRecords(Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        return dateRecordRepository.findTop3ByCoupleIdOrderByCreatedAtDesc(couple.getId())
                .stream()
                .map(record -> {
                    DateRecordRecentResponseDto dto = new DateRecordRecentResponseDto();

                    dto.setId(record.getId());
                    dto.setTitle(record.getTitle());
                    dto.setDateRecordDate(record.getDateRecordDate());

                    List<DateRecordImage> images =
                            dateRecordImageRepository.findByDateRecordId(record.getId());

                    if (!images.isEmpty()) {
                        dto.setThumbnailImageUrl(images.get(0).getImageUrl());
                    } else {
                        dto.setThumbnailImageUrl("/images/default-record.png");
                    }

                    return dto;
                })
                .toList();
    }

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

        List<DateRecordExpenseResponseDto> expenses = dateRecordExpenseRepository.findByDateRecordId(dateRecord.getId())
                        .stream()
                        .map(dateRecordExpense -> {
                            Expense expense = dateRecordExpense.getExpense();

                            DateRecordExpenseResponseDto dto = new DateRecordExpenseResponseDto();
                            dto.setId(expense.getId());
                            dto.setContent(expense.getContent());
                            dto.setAmount(expense.getAmount());
                            dto.setCategoryName(expense.getCategory().getDisplayName());

                            return dto;
                        })
                        .toList();

        int totalExpenseAmount = expenses.stream()
                .mapToInt(DateRecordExpenseResponseDto::getAmount)
                .sum();

        detailResponseDto.setExpenses(expenses);
        detailResponseDto.setTotalExpenseAmount(totalExpenseAmount);

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

        // 삭제 체크한 사진만 삭제
        if (updateRequestDto.getDeleteImageIds() != null) {
            for (Long imageId : updateRequestDto.getDeleteImageIds()) {
                dateRecordImageRepository.deleteByIdAndDateRecordId(imageId, dateRecord.getId());
            }
        }

        // 새로 선택한 사진 추가
        if (updateRequestDto.getImages() != null) {
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

    public void connectExpenses(Long recordId, List<Long> expenseIds, Member member) {

        CoupleMember coupleMember = coupleMemberRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("커플 정보를 찾을 수 없습니다."));

        Couple couple = coupleMember.getCouple();

        DateRecord dateRecord = dateRecordRepository.findByIdAndCoupleId(recordId, couple.getId())
                .orElseThrow(() -> new IllegalArgumentException("데이트 기록을 찾을 수 없습니다."));

        for (Long expenseId : expenseIds) {
            if (dateRecordExpenseRepository.existsByDateRecordIdAndExpenseId(recordId, expenseId)) {
                continue;
            }

            Expense expense = expenseRepository.findByIdAndCoupleId(expenseId, couple.getId()).orElseThrow(() -> new IllegalArgumentException("비용 정보를 찾을 수 없습니다."));

            DateRecordExpense dateRecordExpense = DateRecordExpense.create(dateRecord, expense);

            dateRecordExpenseRepository.save(dateRecordExpense);
        }
    }
}
