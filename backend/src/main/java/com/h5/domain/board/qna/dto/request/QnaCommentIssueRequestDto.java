package com.h5.domain.board.qna.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QnaCommentIssueRequestDto {
    private int qnaId;
    private String content;
}
