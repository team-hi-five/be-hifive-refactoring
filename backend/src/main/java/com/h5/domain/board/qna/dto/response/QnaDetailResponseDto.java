package com.h5.domain.board.qna.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaDetailResponseDto {
    private Integer id;
    private String title;
    private String content;
    private String name;
    private String createDttm;
    private Integer answerCnt;

    private List<QnaCommentResponseDto> qnaAnswerResponseList;
}
