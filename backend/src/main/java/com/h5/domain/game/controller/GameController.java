package com.h5.domain.game.controller;

import com.h5.domain.game.dto.request.*;
import com.h5.domain.game.dto.response.*;
import com.h5.domain.game.service.GameService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Game API", description = "게임 챕터·스테이지·로그 관리 API")
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @Operation(
            summary = "게임 챕터 시작",
            description = "자녀의 새로운 게임 챕터를 시작하고, 생성된 챕터 ID를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "챕터 시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 챕터 정보를 찾을 수 없음")
    })
    @PostMapping("/chapters")
    public ResultResponse<StartGameChapterResponse> startGameChapter(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "시작할 게임 챕터 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StartGameChapterRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody
            StartGameChapterRequest startGameChapterRequest
    ) {
        return ResultResponse.success(gameService.startGameChapter(startGameChapterRequest));
    }

    @Operation(
            summary = "게임 챕터 종료",
            description = "지정된 게임 챕터를 종료하고 통계를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "챕터 종료 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "챕터를 찾을 수 없음")
    })
    @PutMapping("/{childGameChapterId}/end")
    public ResultResponse<EndGameChapterResponse> endGameChapter(
            @Parameter(description = "종료할 챕터의 ID", required = true, example = "123")
            @PathVariable Integer childGameChapterId
    ) {
        return ResultResponse.success(gameService.endGameChapter(childGameChapterId));
    }

    @Operation(
            summary = "게임 스테이지 시작",
            description = "지정된 챕터에 새로운 게임 스테이지를 시작하고, 생성된 스테이지 ID를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스테이지 시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "챕터 또는 스테이지 정보를 찾을 수 없음")
    })
    @PostMapping("/stages")
    public ResultResponse<StartGameStageResponse> startGameStage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "시작할 게임 스테이지 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StartGameStageRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody
            StartGameStageRequest startGameStageRequest
    ) {
        return ResultResponse.success(gameService.startGameStage(startGameStageRequest));
    }

    @Operation(
            summary = "게임 로그 저장",
            description = "특정 스테이지에 대한 게임 플레이 로그와 AI 분석 결과를 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "관련 엔티티를 찾을 수 없음")
    })
    @PostMapping("/logs")
    public ResultResponse<SaveGameLogResponse> saveGameLog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "저장할 게임 로그 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SaveGameLogRequest.class))
            )
            @Valid @RequestBody
            SaveGameLogRequest saveGameLogRequest
    ) {
        return ResultResponse.success(gameService.saveGameLog(saveGameLogRequest));
    }
}
