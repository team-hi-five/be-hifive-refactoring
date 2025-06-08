package com.h5.domain.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "스케줄 생성 응답 DTO")
public class IssueScheduleResponse {

    @Schema(description = "생성된 스케줄 ID", example = "1")
    private final Integer scheduleId;
}
