package com.h5.domain.user.child.controller;

import com.h5.domain.user.child.dto.request.UpdateChildStageRequest;
import com.h5.domain.user.child.dto.response.UpdateChildStageResponse;
import com.h5.domain.user.child.service.ChildUserService;
import com.h5.domain.user.child.dto.request.ModifyChildRequestDto;
import com.h5.domain.user.child.dto.response.ModifyChildResponse;
import com.h5.domain.user.child.dto.response.SearchChildResponse;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildUserController {

    private final ChildUserService childUserService;

    @Operation(
            summary  = "특정 자녀 정보 수정",
            description = "자녀 ID(childUserId)와 수정할 정보를 담은 DTO를 받아 자녀 정보를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자녀 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "해당 자녀를 찾을 수 없음")
    })
    @PutMapping("/{childUserId}")
    public ResultResponse<ModifyChildResponse> modifyChild(
            @Parameter(description = "수정할 자녀 사용자 ID", example = "123")
            @Valid @PathVariable int childUserId,
            @Parameter(description = "자녀 정보 수정 요청 DTO", required = true)
            @Valid @RequestBody ModifyChildRequestDto dto
    ) {
        return ResultResponse.success(childUserService.updateChild(dto, childUserId));
    }

    @Operation(
            summary  = "자녀 이름으로 검색",
            description = "쿼리 파라미터(name)로 전달된 자녀 이름을 포함한 자녀 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자녀 검색 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터가 유효하지 않음")
    })
    @GetMapping
    public ResultResponse<List<SearchChildResponse>> searchChild(
            @Parameter(description = "검색할 자녀 이름", example = "김철수")
            @Valid @RequestParam String name
    ) {
        return ResultResponse.success(childUserService.searchChildByName(name));
    }

    @Operation(
            summary = "자녀 단계 업데이트",
            description = "지정된 자녀(userId)에 대해 학습 단계(study stage)를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "단계 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "해당 자녀를 찾을 수 없음")
    })
    @PutMapping("/{childUserId}/stage")
    public ResultResponse<UpdateChildStageResponse> updateChildStage(
            @Parameter(description = "단계를 업데이트할 자녀의 사용자 ID", required = true, example = "42")
            @PathVariable Integer childUserId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업데이트할 자녀 단계 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateChildStageRequest.class)
                    )
            )
            @RequestBody @Valid UpdateChildStageRequest updateChildStageRequest
    ) {
        return ResultResponse.success(childUserService.updateChildStage(childUserId, updateChildStageRequest));
    }

}
