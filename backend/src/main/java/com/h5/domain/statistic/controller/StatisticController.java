package com.h5.domain.statistic.controller;

import com.h5.domain.statistic.dto.response.DataAnalysisResponse;
import com.h5.domain.statistic.dto.response.GetGameVideoDatesResponse;
import com.h5.domain.statistic.dto.response.GetGameVideoLengthResponse;
import com.h5.domain.statistic.service.StatisticService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Statistic API", description = "아동 통계 조회 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    @Operation(
            summary = "감정별 통계 분석 결과 조회",
            description = "아동 사용자 ID를 전달받아, 각 감정별 평점 및 시도/정답 통계를 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/analysis")
    public ResultResponse<Map<Integer, DataAnalysisResponse>> dataAnalysis(
            @Parameter(description = "아동 사용자 ID", required = true, example = "123")
            @Valid @RequestParam Integer childUserId
    ) {
        return ResultResponse.success(statisticService.dataAnalysis(childUserId));
    }

    @Operation(
            summary = "게임 챕터 시작 날짜 목록 조회",
            description = "지정된 연월 내에 아동 사용자가 시작한 게임 챕터의 날짜 목록을 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{childUserId}/dates")
    public ResultResponse<GetGameVideoDatesResponse> getGameVideoDates(
            @Parameter(description = "아동 사용자 ID", example = "123")
            @PathVariable Integer childUserId,
            @Parameter(description = "조회할 연도 (YYYY)", required = true, example = "2025")
            @Valid @RequestParam Integer year,
            @Parameter(description = "조회할 월 (1~12)", required = true, example = "6")
            @Valid @RequestParam Integer month
    ) {
        return ResultResponse.success(statisticService.getGameVideoDates(childUserId, year, month));
    }

    @Operation(
            summary = "게임 영상 재생 길이(시도) 조회",
            description = "지정된 연월 및 스테이지에 대한 아동 사용자의 게임 재생 시도 인덱스와 로그 ID를 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{childUserId}/{stageId}/length")
    public ResultResponse<List<GetGameVideoLengthResponse>> getGameVideosLength(
            @Parameter(description = "아동 사용자 ID", example = "123")
            @PathVariable Integer childUserId,
            @Parameter(description = "게임 스테이지 ID", required = true, example = "5")
            @PathVariable Integer stageId,
            @Parameter(description = "조회할 연도 (YYYY)", required = true, example = "2025")
            @Valid @RequestParam Integer year,
            @Parameter(description = "조회할 월 (1~12)", required = true, example = "6")
            @Valid @RequestParam Integer month
    ) {
        return ResultResponse.success(statisticService.getGameVideoLength(childUserId, stageId, year, month));
    }

}
