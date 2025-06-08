package com.h5.domain.board.faq.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * FAQ 목록 조회 응답 DTO.
 */
@Getter
@AllArgsConstructor
@Builder
@Schema(name = "FaqListResponseDto", description = "페이징된 FAQ 목록 응답 DTO")
public class FaqListResponse {

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private final Integer currentPage;

    @Schema(description = "페이지 당 요소 개수", example = "10")
    private final Integer pageSize;

    @Schema(description = "전체 페이지 수", example = "5")
    private final Integer totalPages;

    @Schema(description = "전체 요소 수", example = "50")
    private final Long totalElements;

    @Schema(description = "조회된 FAQ 상세 목록")
    private final List<FaqDetailResponse> faqs;
}
