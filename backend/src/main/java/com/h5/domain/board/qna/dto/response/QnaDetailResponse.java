package com.h5.domain.board.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "QnaDetailResponse", description = "QnA 게시글 상세 조회 응답 DTO")
public class QnaDetailResponse {

    @Schema(description = "QnA 게시글 식별자", example = "123")
    private final Integer id;

    @Schema(description = "QnA 제목", example = "아이 학습 상태 문의")
    private final String title;

    @Schema(description = "QnA 내용", example = "우리 아이가 수업을 잘 따라가고 있는지 알고 싶습니다.")
    private final String content;

    @Schema(description = "작성자 이름", example = "홍길동")
    private final String name;

    @Schema(description = "작성 일시", example = "2025-06-09T14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime issuedAt;

    @Schema(description = "댓글 수", example = "2")
    private final Integer commentCount;

    @Schema(description = "댓글 응답 목록")
    private final List<QnaCommentResponse> qnaCommentResponseList;
}
