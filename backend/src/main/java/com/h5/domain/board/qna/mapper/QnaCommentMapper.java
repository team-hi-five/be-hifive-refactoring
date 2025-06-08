package com.h5.domain.board.qna.mapper;

import com.h5.domain.board.qna.dto.response.QnaCommentResponse;
import com.h5.domain.board.qna.entity.QnaCommentEntity;
import com.h5.domain.file.entity.FileEntity;
import com.h5.global.helper.FileUrlHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QnaCommentMapper {

    private final FileUrlHelper fileUrlHelper;

    public QnaCommentResponse toDetailResponse(QnaCommentEntity qnaCommentEntity) {
        return QnaCommentResponse.builder()
                .id(qnaCommentEntity.getId())
                .content(qnaCommentEntity.getContent())
                .issuedAt(qnaCommentEntity.getIssuedAt())
                .name(qnaCommentEntity.getConsultantUser().getName())
                .profileImageUrl(fileUrlHelper.getProfileUrlOrDefault(FileEntity.TblType.PCT, qnaCommentEntity.getConsultantUser().getId()))
                .build();
    }
}
