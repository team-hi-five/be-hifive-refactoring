package com.h5.domain.board.qna.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaCommentResponseDto {
    private int id;
    private String content;
    private String createAt;
    private String name;
    private String profileImageUrl;
}
