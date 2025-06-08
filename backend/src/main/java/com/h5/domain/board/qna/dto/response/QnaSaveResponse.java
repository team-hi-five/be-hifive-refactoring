package com.h5.domain.board.qna.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "QnaSaveResponse", description = "QnA 저장(등록/수정) 응답 DTO")
public class QnaSaveResponse {

    @Schema(description = "저장된 QnA의 ID", example = "123")
    private final Integer qnaId;
}
