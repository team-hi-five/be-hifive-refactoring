package com.h5.domain.board.qna.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "QnaListResponse", description = "페이징된 QnA 목록 조회 응답 DTO")
public class QnaListResponse {

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private final Integer currentPage;

    @Schema(description = "페이지 당 요소 개수", example = "10")
    private final Integer pageSize;

    @Schema(description = "전체 페이지 수", example = "5")
    private final Integer totalPages;

    @Schema(description = "전체 요소(게시글) 수", example = "42")
    private final Long totalElements;

    @Schema(description = "조회된 QnA 상세 목록")
    private final List<QnaDetailResponse> qnaList;
}
