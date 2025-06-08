package com.h5.domain.board.qna.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaCommentUpdateRequestDto {
    private String content;
}
