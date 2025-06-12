package com.h5.domain.board.qna.mapper;

import com.h5.domain.board.qna.dto.response.QnaCommentResponse;
import com.h5.domain.board.qna.dto.response.QnaDetailResponse;
import com.h5.domain.board.qna.dto.response.QnaListResponse;
import com.h5.domain.board.qna.entity.QnaCommentEntity;
import com.h5.domain.board.qna.entity.QnaEntity;
import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.entity.TblType;
import com.h5.global.file.FileUrlHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QnaMapper {

    private final FileUrlHelper fileUrlHelper;

    public QnaListResponse toListResponse(Page<QnaEntity> qnaPage) {
        List<QnaDetailResponse> qnaResponses = qnaPage.getContent().stream()
                .map(qnaEntity -> QnaDetailResponse.builder()
                        .id(qnaEntity.getId())
                        .title(qnaEntity.getTitle())
                        .content(qnaEntity.getContent())
                        .name(qnaEntity.getParentUser().getName())
                        .issuedAt(qnaEntity.getIssuedAt())
                        .commentCount(qnaEntity.getCommentCount())
                        .build()
                )
                .collect(Collectors.toList());

        return QnaListResponse.builder()
                .currentPage(qnaPage.getNumber())
                .pageSize(qnaPage.getSize())
                .totalElements(qnaPage.getTotalElements())
                .totalPages(qnaPage.getTotalPages())
                .qnaList(qnaResponses)
                .build();
    }

    public QnaDetailResponse toDetailResponse(QnaEntity qnaEntity, List<QnaCommentEntity> qnaCommentList) {
        List<QnaCommentResponse> qnaCommentResponseList = qnaCommentList.stream()
                .map(commentEntity -> QnaCommentResponse.builder()
                        .id(commentEntity.getId())
                        .content(commentEntity.getContent())
                        .issuedAt(commentEntity.getIssuedAt())
                        .name(commentEntity.getConsultantUser().getName())
                        .profileImageUrl(fileUrlHelper.getProfileUrlOrDefault(TblType.PCT, commentEntity.getConsultantUser().getId()))
                        .build()
                )
                .toList();

        return QnaDetailResponse.builder()
                .id(qnaEntity.getId())
                .title(qnaEntity.getTitle())
                .content(qnaEntity.getContent())
                .name(qnaEntity.getParentUser().getName())
                .issuedAt(qnaEntity.getIssuedAt())
                .commentCount(qnaEntity.getCommentCount())
                .qnaCommentResponseList(qnaCommentResponseList)
                .build();
    }
}
