package com.h5.domain.board.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "QnaCommentResponse", description = "QnA 댓글 응답 DTO")
public class QnaCommentResponse {

    @Schema(description = "댓글 식별자", example = "456")
    private final Integer id;

    @Schema(description = "댓글 내용", example = "좋은 답변 감사합니다!")
    private final String content;

    @Schema(description = "댓글 작성 일시", example = "2025-06-09 14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime issuedAt;

    @Schema(description = "댓글 작성자 이름", example = "상담사 김철수")
    private final String name;

    @Schema(description = "댓글 작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;
}
