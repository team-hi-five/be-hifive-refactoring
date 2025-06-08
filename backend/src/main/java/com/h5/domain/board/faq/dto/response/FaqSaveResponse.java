package com.h5.domain.board.faq.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "FaqSaveResponseDto", description = "FAQ 저장(등록/수정) 응답 DTO")
public class FaqSaveResponse {

    @Schema(description = "저장된 FAQ의 ID", example = "123")
    private final Integer id;
}
