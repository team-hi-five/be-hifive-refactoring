package com.h5.domain.board.qna.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * QnA 댓글 수정 요청을 위한 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(name = "QnaCommentUpdateRequest", description = "QnA 댓글 수정 요청 DTO")
public class QnaCommentUpdateRequest {

    @NotBlank(message = "댓글 내용을 입력해야 합니다.")
    @Size(max = 1000, message = "댓글은 최대 1000자까지 가능합니다.")
    @Schema(description = "수정할 댓글 내용", example = "수정된 답변 내용입니다.")
    private String content;
}
