package com.h5.domain.study.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "학습 스테이지 시작 요청 DTO")
public class StartStudyStageRequest {

    @Schema(description = "시작할 게임 스테이지 ID", example = "10")
    @NotNull(message = "gameStageId는 필수 입력값입니다.")
    private Integer gameStageId;

    @Schema(description = "학습 챕터 ID (ChildStudyChapter)", example = "3")
    @NotNull(message = "childStudyChapterId는 필수 입력값입니다.")
    private Integer childStudyChapterId;
}
