package com.h5.domain.schedule.controller;

import com.h5.domain.schedule.dto.request.ScheduleIssueRequest;
import com.h5.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.h5.domain.schedule.dto.response.IssueScheduleResponse;
import com.h5.domain.schedule.dto.response.ScheduleDatesResponse;
import com.h5.domain.schedule.dto.response.ScheduleResponse;
import com.h5.domain.schedule.service.ScheduleService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule API", description = "스케줄 관련 기능")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(
            summary = "날짜별 스케줄 조회",
            description = "특정 날짜에 해당하는 상담 및 게임 스케줄 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 스케줄 목록을 반환했습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping
    public ResultResponse<List<ScheduleResponse>> getSchedulesByDate(
            @Parameter(description = "조회할 날짜", example = "2025-06-09")
            @RequestParam LocalDate date
    ) {
        return ResultResponse.success(scheduleService.getSchedulesByDate(date));
    }

    @Operation(
            summary = "어린이별 일정 날짜 조회",
            description = "어린이 ID와 연·월을 기반으로 예약된 날짜 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 예약 일자 목록을 반환했습니다."),
            @ApiResponse(responseCode = "404", description = "해당 어린이를 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/{childUserId}/date")
    public ResultResponse<ScheduleDatesResponse> getScheduleDatesByChildId(
            @Parameter(description = "어린이 사용자 ID", example = "5")
            @PathVariable Integer childUserId,
            @Parameter(description = "조회 연도", example = "2025")
            @RequestParam Integer year,
            @Parameter(description = "조회 월", example = "6")
            @RequestParam Integer month
    ) {
        return ResultResponse.success(
                scheduleService.getScheduleDatesByChildUserId(childUserId, year, month)
        );
    }

    @Operation(
            summary = "어린이별 스케줄 조회",
            description = "어린이 ID와 연·월을 기반으로 상세 스케줄 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 상세 스케줄 목록을 반환했습니다."),
            @ApiResponse(responseCode = "404", description = "해당 어린이를 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/{childUserId}/schedule")
    public ResultResponse<List<ScheduleResponse>> getSchedulesByChildId(
            @Parameter(description = "어린이 사용자 ID", example = "5")
            @PathVariable Integer childUserId,
            @Parameter(description = "조회 연도", example = "2025")
            @RequestParam Integer year,
            @Parameter(description = "조회 월", example = "6")
            @RequestParam Integer month
    ) {
        return ResultResponse.success(
                scheduleService.getSchedulesByChildUserId(childUserId, year, month)
        );
    }

    @Operation(
            summary = "가능 시간 조회",
            description = "상담 가능한 시간을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 가능 시간을 반환했습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/available")
    public ResultResponse<List<LocalTime>> getAvailableTimes(
            @Parameter(description = "조회할 날짜", example = "2025-06-09")
            @RequestParam LocalDate date
    ) {
        return ResultResponse.success(scheduleService.getAvailableTimes(date));
    }

    @Operation(
            summary = "스케줄 생성",
            description = "새로운 상담 또는 게임 스케줄을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "스케줄이 성공적으로 생성되었습니다."),
            @ApiResponse(responseCode = "409", description = "스케줄 충돌이 발생했습니다."),
            @ApiResponse(responseCode = "404", description = "대상 어린이를 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @PostMapping
    public ResultResponse<IssueScheduleResponse> createSchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 스케줄 정보"
            )
            @RequestBody ScheduleIssueRequest scheduleIssueRequest
    ) {
        return ResultResponse.created(
                scheduleService.issueSchedule(scheduleIssueRequest)
        );
    }

    @Operation(
            summary = "스케줄 수정",
            description = "기존 상담 또는 게임 스케줄의 일시 및 대상 어린이를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스케줄이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "404", description = "스케줄 또는 대상 어린이를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "스케줄 충돌이 발생했습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @PutMapping("/{id}/{childUserId}")
    public ResultResponse<IssueScheduleResponse> updateSchedule(
            @Parameter(description = "수정할 스케줄 ID", example = "123")
            @PathVariable Integer id,
            @Parameter(description = "새 어린이 사용자 ID", example = "5")
            @PathVariable Integer childUserId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 스케줄 정보"
            )
            @RequestBody ScheduleUpdateRequest scheduleUpdateRequest
    ) {
        return ResultResponse.success(
                scheduleService.updateSchedule(id, childUserId, scheduleUpdateRequest)
        );
    }

    @Operation(
            summary = "스케줄 삭제",
            description = "상담 또는 게임 스케줄을 soft delete 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스케줄이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 스케줄을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @DeleteMapping("/{type}/{scheduleId}")
    public ResultResponse<Void> deleteSchedule(
            @Parameter(description = "스케줄 타입 (consult 또는 game)", example = "consult")
            @PathVariable String type,
            @Parameter(description = "삭제할 스케줄 ID", example = "123")
            @PathVariable Integer scheduleId
    ) {
        scheduleService.deleteSchedule(type, scheduleId);
        return ResultResponse.success();
    }

    @Operation(
            summary = "부모별 스케줄 조회 (날짜)",
            description = "부모 사용자 연·월 기준으로 예약된 스케줄 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 스케줄 목록을 반환했습니다.")
    })
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @GetMapping("/parents")
    public ResultResponse<List<ScheduleResponse>> getSchedulesByParentId(
            @Parameter(description = "조회 연도", example = "2025")
            @RequestParam Integer year,
            @Parameter(description = "조회 월", example = "6")
            @RequestParam Integer month
    ) {
        return ResultResponse.success(
                scheduleService.getSchedulesByParentUserId(year, month)
        );
    }

    @GetMapping("/parents/date")
    @Operation(
            summary = "부모별 일정 날짜 조회",
            description = "부모 사용자 연·월 기준으로 예약된 날짜 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 예약 일자 목록을 반환했습니다.")
    })
    public ResultResponse<ScheduleDatesResponse> getScheduleDatesByParentId(
            @Parameter(description = "조회 연도", example = "2025")
            @RequestParam Integer year,
            @Parameter(description = "조회 월", example = "6")
            @RequestParam Integer month
    ) {
        return ResultResponse.success(
                scheduleService.getScheduleDatesByParentUserId(year, month)
        );
    }

}
