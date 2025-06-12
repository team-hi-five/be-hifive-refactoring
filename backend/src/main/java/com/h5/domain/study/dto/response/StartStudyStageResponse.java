package com.h5.domain.study.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "학습 스테이지 시작 처리 후 반환되는 DTO")
@Getter
@AllArgsConstructor
@Builder
public class StartStudyStageResponse {

    @Schema(description = "생성된 ChildStudyStage ID", example = "5")
    private final Integer childStudyStageId;
}
