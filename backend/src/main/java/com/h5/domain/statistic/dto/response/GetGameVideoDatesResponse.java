package com.h5.domain.statistic.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "지정된 연월 내에 아동 사용자가 게임 챕터를 시작한 고유 날짜 목록 DTO")
public class GetGameVideoDatesResponse {

    /**
     * 게임 챕터 시작 날짜 목록
     */
    @Schema(
            description = "게임 챕터를 시작한 날짜 목록",
            example = "[\"2025-06-01\",\"2025-06-05\",\"2025-06-10\"]"
    )
    private final List<LocalDate> dateList;
}
