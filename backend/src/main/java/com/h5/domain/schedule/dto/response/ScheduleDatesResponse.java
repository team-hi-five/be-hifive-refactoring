package com.h5.domain.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "스케줄 날짜 응답 DTO")
public class ScheduleDatesResponse {

    @Schema(
            description = "예약된 일시 목록",
            example = "[\"2025-06-09T09:00:00\", \"2025-06-10T10:00:00\"]"
    )
    private final List<LocalDateTime> dateList;
}
