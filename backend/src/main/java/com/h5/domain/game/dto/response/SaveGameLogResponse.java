package com.h5.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게임 로그 저장 응답 DTO")
public class SaveGameLogResponse {

    @Schema(description = "생성된 게임 로그 ID", example = "123")
    private final Integer gameLogId;

    @Schema(description = "생성된 AI 로그 ID", example = "456")
    private final Integer aiLogId;
}
