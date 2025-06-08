package com.h5.domain.game.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "게임 챕터 종료 요청 DTO")
public class EndGameChapterRequest {

    @NotNull(message = "childGameChapterId는 필수 값입니다.")
    @Schema(description = "종료할 어린이 게임 챕터 ID", example = "1")
    private Integer childGameChapterId;
}
