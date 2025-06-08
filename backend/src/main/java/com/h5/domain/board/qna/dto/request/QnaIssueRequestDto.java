package com.h5.domain.board.qna.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaIssueRequestDto {
    private String title;
    private String content;
}
