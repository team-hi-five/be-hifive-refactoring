package com.h5.domain.study.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "비디오 로그와 텍스트 로그 저장 후 반환되는 DTO")
@Getter
@AllArgsConstructor
@Builder
public class SaveStudyLogResponse {

    @Schema(description = "저장된 비디오 로그 ID", example = "101")
    private final Integer studyVideoLogId;

    @Schema(description = "저장된 텍스트 로그 ID", example = "202")
    private final Integer studyTextLogId;
}
