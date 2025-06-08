package com.h5.domain.game.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "게임 스테이지 시작 요청 DTO")
public class StartGameStageRequest {

    @NotNull(message = "gameStageId는 필수 값입니다.")
    @Schema(description = "시작할 게임 스테이지 ID", example = "7")
    private Integer gameStageId;

    @NotNull(message = "childGameChapterId는 필수 값입니다.")
    @Schema(description = "해당 스테이지가 속한 어린이 게임 챕터 ID", example = "1")
    private Integer childGameChapterId;
}
