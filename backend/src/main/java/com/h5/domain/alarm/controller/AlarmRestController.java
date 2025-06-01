package com.h5.domain.alarm.controller;

import com.h5.domain.alarm.dto.request.AlarmRequestDto;
import com.h5.domain.alarm.service.AlarmService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Alarm API", description = "알람 관련 API")
public class AlarmRestController {

    private final AlarmService alarmService;

    @Operation(
            summary = "알람 트리거",
            description = "알람 요청 정보를 받아 해당 사용자에게 알람을 전송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알람 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/")
    public ResultResponse<Void> triggerAlarm(
            @RequestBody(
                    description = "알림을 전송할 대상과 세션 타입 정보를 포함한 DTO",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody AlarmRequestDto alarmRequestDto
    ) {
        alarmService.sendAlarm(alarmRequestDto);
        return ResultResponse.success();
    }
}
