package com.h5.domain.board.qna.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaListResponseDto {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<QnaDetailResponseDto> qnaList;
}
