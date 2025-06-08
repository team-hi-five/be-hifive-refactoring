package com.h5.domain.game.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "게임 챕터 시작 요청 DTO")
public class StartGameChapterRequest {

    @NotNull(message = "childUserId는 필수 값입니다.")
    @Schema(description = "어린이 사용자 ID", example = "5")
    private Integer childUserId;

    @NotNull(message = "gameChapterId는 필수 값입니다.")
    @Schema(description = "시작할 게임 챕터 ID", example = "3")
    private Integer gameChapterId;
}
