package com.h5.domain.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "스케줄 응답 DTO")
public class ScheduleResponse {

    @Schema(description = "스케줄 ID", example = "1")
    private final Integer scheduleId;

    @Schema(description = "예약 일시", example = "2025-06-09T14:00:00")
    private final LocalDateTime scheduleAt;

    @Schema(description = "스케줄 타입 ('consult' 또는 'game')", example = "consult")
    private final String type;

    @Schema(description = "상담사 이름", example = "김상담")
    private final String consultantName;

    @Schema(description = "어린이 사용자 ID", example = "5")
    private final Integer childUserId;

    @Schema(description = "어린이 이름", example = "홍철수")
    private final String childName;

    @Schema(description = "부모 이름", example = "김부모")
    private final String parentName;

    @Schema(description = "부모 이메일", example = "parent@example.com")
    private final String parentEmail;

    @Schema(description = "스케줄 상태", example = "P")
    private final String status;
}
