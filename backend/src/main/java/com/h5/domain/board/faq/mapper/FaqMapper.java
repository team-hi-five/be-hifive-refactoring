package com.h5.domain.board.faq.mapper;

import com.h5.domain.board.faq.dto.response.FaqDetailResponse;
import com.h5.domain.board.faq.dto.response.FaqListResponse;
import com.h5.domain.board.faq.entity.FaqEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FaqMapper {
    public FaqListResponse toListResponse(Page<FaqEntity> faqPage) {
        List<FaqDetailResponse> faqResponses = faqPage.getContent().stream()
                .map(faqEntity -> FaqDetailResponse.builder()
                        .id(faqEntity.getId())
                        .title(faqEntity.getTitle())
                        .content(faqEntity.getContent())
                        .writer(faqEntity.getConsultantUser().getName())
                        .type(faqEntity.getType())
                        .build()
                )
                .collect(Collectors.toList());


        return FaqListResponse.builder()
                .currentPage(faqPage.getNumber())
                .pageSize(faqPage.getSize())
                .totalElements(faqPage.getTotalElements())
                .totalPages(faqPage.getTotalPages())
                .faqs(faqResponses)
                .build();
    }

    public FaqDetailResponse toDetailResponse(FaqEntity faqEntity) {
        return FaqDetailResponse.builder()
                .id(faqEntity.getId())
                .title(faqEntity.getTitle())
                .content(faqEntity.getContent())
                .writer(faqEntity.getConsultantUser().getName())
                .type(faqEntity.getType())
                .build();
    }
}
