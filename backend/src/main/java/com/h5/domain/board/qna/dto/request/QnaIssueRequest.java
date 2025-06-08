package com.h5.domain.board.qna.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "QnaIssueRequest", description = "QnA 게시글 작성 요청 DTO")
public class QnaIssueRequest {

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    @Schema(description = "QnA 제목", example = "아이의 학습 상태를 알고 싶습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Size(max = 2000, message = "내용은 최대 2000자까지 가능합니다.")
    @Schema(description = "QnA 내용", example = "우리 아이가 수업을 잘 따라가고 있는지 알고 싶습니다.")
    private String content;
}
