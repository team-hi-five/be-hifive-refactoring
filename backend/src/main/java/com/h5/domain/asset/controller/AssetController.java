package com.h5.domain.asset.controller;

import com.h5.domain.asset.dto.response.GetStageResponseDto;
import com.h5.domain.asset.dto.response.LoadAssetResponseDto;
import com.h5.domain.asset.dto.response.LoadCardResponseDto;
import com.h5.domain.asset.dto.response.LoadChapterAssetResponseDto;
import com.h5.domain.asset.service.AssetService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
@Tag(name = "Asset API", description = "게임 및 카드 자산 조회 관련 API")
public class AssetController {

    private final AssetService assetService;

    @Operation(
            summary = "현재 클리어한 스테이지의 게임 자산 조회",
            description = "자녀 ID로 해당 자녀가 현재 클리어한 스테이지에 해당하는 게임 자산을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자녀 또는 게임 자산을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/child/{childUserId}")
    public ResultResponse<LoadAssetResponseDto> getCurrentAsset(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @PathVariable int childUserId
    ) {
        return ResultResponse.success(assetService.loadAsset(childUserId));
    }

    @Operation(
            summary = "클리어한 스테이지 이하 카드 자산 목록 조회",
            description = "자녀 ID로 해당 자녀가 클리어한 스테이지 이하의 모든 카드 자산 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자녀를 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/child/{childUserId}/cards")
    public ResultResponse<LoadCardResponseDto> getCards(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @PathVariable int childUserId
    ) {
        return ResultResponse.success(assetService.loadCards(childUserId));
    }

    @Operation(
            summary = "이용 가능한 챕터 목록 조회",
            description = "자녀 ID로 해당 자녀가 이용 가능한 챕터 목록과 한도 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자녀를 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/child/{childUserId}/chapters")
    public ResultResponse<LoadChapterAssetResponseDto> getChapterAsset(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @PathVariable int childUserId
    ) {
        return ResultResponse.success(assetService.loadChapterAsset(childUserId));
    }

    @Operation(
            summary = "현재 클리어된 챕터/스테이지 정보 조회",
            description = "자녀 ID로 해당 자녀의 현재 클리어된 챕터 번호와 스테이지 번호를 반환합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "자녀를 찾을 수 없음")
    })
    @GetMapping("/child/{childUserId}/stage-info")
    public ResultResponse<GetStageResponseDto> getStageInfo(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @PathVariable int childUserId
    ) {
        return ResultResponse.success(assetService.getStage(childUserId));
    }

    @Operation(
            summary = "특정 챕터·스테이지의 게임 자산 조회",
            description = "URL Path에 포함된 챕터, 스테이지 번호로 해당하는 게임 자산을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게임 자산을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{chapter}/{stage}")
    public ResultResponse<LoadAssetResponseDto> getAssetByStage(
            @Parameter(description = "조회할 챕터 번호", example = "2")
            @PathVariable int chapter,
            @Parameter(description = "조회할 스테이지 번호", example = "3")
            @PathVariable int stage
    ) {
        return ResultResponse.success(assetService.loadAssetByStage(chapter, stage));
    }

    @Operation(
            summary = "학습용(스터디) 자산 목록 조회",
            description = "URL Path에 포함된 챕터 번호로 해당 챕터의 학습용 자산 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "학습용 자산을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/study/{chapter}")
    public ResultResponse<List<LoadAssetResponseDto>> getStudyAsset(
            @Parameter(description = "조회할 챕터 번호", example = "2")
            @PathVariable int chapter
    ) {
        return ResultResponse.success(assetService.loadStudyAsset(chapter));
    }
}
