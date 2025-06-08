package com.h5.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게임 챕터 시작 응답 DTO")
public class StartGameChapterResponse {

    @Schema(description = "생성된 어린이 게임 챕터 ID", example = "1")
    private final Integer childGameChapterId;
}
