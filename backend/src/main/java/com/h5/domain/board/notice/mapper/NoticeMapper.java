package com.h5.domain.board.notice.mapper;

import com.h5.domain.board.notice.dto.response.NoticeDetailResponse;
import com.h5.domain.board.notice.dto.response.NoticeListResponse;
import com.h5.domain.board.notice.entity.NoticeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NoticeMapper {

    public NoticeListResponse toListResponse(Page<NoticeEntity> noticePage) {
        List<NoticeDetailResponse> noticeResponses = noticePage.getContent().stream()
                .map(noticeEntity -> NoticeDetailResponse.builder()
                        .id(noticeEntity.getId())
                        .title(noticeEntity.getTitle())
                        .content(noticeEntity.getContent())
                        .name(noticeEntity.getConsultantUser().getName())
                        .issuedAt(noticeEntity.getIssuedAt())
                        .viewCnt(noticeEntity.getViewCnt())
                        .build()
                )
                .collect(Collectors.toList());

        return NoticeListResponse.builder()
                .currentPage(noticePage.getNumber())
                .pageSize(noticePage.getSize())
                .totalElements(noticePage.getTotalElements())
                .totalPages(noticePage.getTotalPages())
                .notices(noticeResponses)
                .build();
    }

    public NoticeDetailResponse toDetailResponse(NoticeEntity noticeEntity) {
        return NoticeDetailResponse.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .name(noticeEntity.getConsultantUser().getName())
                .issuedAt(noticeEntity.getIssuedAt())
                .viewCnt(noticeEntity.getViewCnt())
                .build();
    }
}
