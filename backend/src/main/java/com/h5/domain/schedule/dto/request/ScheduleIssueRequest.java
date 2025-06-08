package com.h5.domain.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "스케줄 생성 요청 DTO")
public class ScheduleIssueRequest {

    @NotNull(message = "childId는 필수 값입니다.")
    @Schema(description = "예약 대상 어린이 사용자 ID", example = "5")
    private Integer childId;

    @NotBlank(message = "type은 필수 값입니다.")
    @Schema(
            description = "스케줄 타입 ('consult' 또는 'game')",
            example = "consult"
    )
    private String type;

    @NotNull(message = "scheduleAt은 필수 값입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "예약 일시 (yyyy-MM-dd HH:mm:ss)",
            example = "2025-06-09 14:00:00"
    )
    private LocalDateTime scheduleAt;
}
