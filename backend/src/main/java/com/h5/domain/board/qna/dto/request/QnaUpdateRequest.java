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
@Schema(name = "QnaUpdateRequest", description = "QnA 게시글 수정 요청 DTO")
public class QnaUpdateRequest {

    @NotNull(message = "QnA 게시글 ID를 입력해야 합니다.")
    @Schema(description = "수정할 QnA 게시글의 ID", example = "123")
    private Integer id;

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    @Schema(description = "수정할 QnA 제목", example = "아이 학습 상태 문의")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Size(max = 2000, message = "내용은 최대 2000자까지 가능합니다.")
    @Schema(description = "수정할 QnA 내용", example = "우리 아이 전화로 확인 부탁드립니다.")
    private String content;
}
