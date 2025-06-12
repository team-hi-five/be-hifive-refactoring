package com.h5.domain.study.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "학습 챕터 종료 처리 후 반환되는 DTO")
@Getter
@AllArgsConstructor
@Builder
public class EndStudyChapterResponse {

    @Schema(description = "종료된 학습 챕터 ID", example = "1")
    private final Integer childStudyChapterId;
}
