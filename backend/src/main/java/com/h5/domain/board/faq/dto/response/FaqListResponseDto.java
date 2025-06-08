package com.h5.domain.board.faq.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaqListResponseDto {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<FaqDetailResponseDto> faqs;
}
