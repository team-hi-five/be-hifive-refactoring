package com.h5.domain.board.qna.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "QnaCommentIssueRequest", description = "QnA 댓글 작성 요청 DTO")
public class QnaCommentIssueRequest {

    @NotNull(message = "댓글을 작성할 QnA 게시글 ID를 입력해야 합니다.")
    @Schema(description = "대상 QnA 게시글의 ID", example = "123")
    private Integer qnaId;

    @NotBlank(message = "댓글 내용을 입력해야 합니다.")
    @Size(max = 1000, message = "댓글은 최대 1000자까지 가능합니다.")
    @Schema(description = "댓글 내용", example = "답변 감사합니다!")
    private String content;
}
