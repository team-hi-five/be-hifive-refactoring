package com.h5.domain.board.faq.mapper;

import com.h5.domain.board.faq.dto.response.FaqDetailResponseDto;
import com.h5.domain.board.faq.dto.response.FaqListResponseDto;
import com.h5.domain.board.faq.entity.FaqEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FaqMapper {
    public FaqListResponseDto toListResponse(Page<FaqEntity> faqPage) {
        List<FaqDetailResponseDto> faqResponses = faqPage.getContent().stream()
                .map(faqEntity -> FaqDetailResponseDto.builder()
                        .id(faqEntity.getId())
                        .title(faqEntity.getTitle())
                        .content(faqEntity.getContent())
                        .writer(faqEntity.getConsultantUser().getName())
                        .type(faqEntity.getType())
                        .build()
                )
                .collect(Collectors.toList());


        return FaqListResponseDto.builder()
                .currentPage(faqPage.getNumber())
                .pageSize(faqPage.getSize())
                .totalElements(faqPage.getTotalElements())
                .totalPages(faqPage.getTotalPages())
                .faqs(faqResponses)
                .build();
    }

    public FaqDetailResponseDto toDetailResponse(FaqEntity faqEntity) {
        return FaqDetailResponseDto.builder()
                .id(faqEntity.getId())
                .title(faqEntity.getTitle())
                .content(faqEntity.getContent())
                .writer(faqEntity.getConsultantUser().getName())
                .type(faqEntity.getType())
                .build();
    }
}
