package com.h5.domain.board.notice.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeListResponseDto {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<NoticeDetailResponseDto> notices;
}
