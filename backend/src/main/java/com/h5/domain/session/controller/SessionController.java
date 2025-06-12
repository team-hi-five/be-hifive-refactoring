package com.h5.domain.session.controller;

import com.h5.domain.session.dto.request.CloseSessionRequest;
import com.h5.domain.session.dto.request.JoinSessionRequest;
import com.h5.domain.session.dto.response.JoinSessionResponse;
import com.h5.domain.session.service.SessionService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Session API", description = "게임 및 상담 세션 참여 및 종료 기능")
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "회의 참여 또는 생성", description = "주어진 스케줄 ID와 타입에 해당하는 회의 세션에 참여하거나, 세션이 없다면 새로 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = JoinSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "스케줄을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "회의가 시작 전이거나 이미 종료된 상태", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResultResponse<JoinSessionResponse> joinOrCreateMeeting(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회의 참여/생성을 위한 요청 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = JoinSessionRequest.class))
            )
            @RequestBody JoinSessionRequest joinSessionRequest
    ) {
        return ResultResponse.success(
                sessionService.joinOrCreateMeeting(joinSessionRequest)
        );
    }

    @Operation(summary = "회의 종료 처리", description = "주어진 스케줄 ID와 타입에 해당하는 회의를 종료 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "스케줄을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 종료된 회의", content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @PutMapping
    public ResultResponse<Void> endMeeting(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회의 종료 요청 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CloseSessionRequest.class))
            )
            @RequestBody CloseSessionRequest closeSessionRequest
    ) {
        sessionService.endMeeting(closeSessionRequest);
        return ResultResponse.success();
    }
}
