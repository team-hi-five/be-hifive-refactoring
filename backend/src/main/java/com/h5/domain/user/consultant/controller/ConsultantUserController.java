package com.h5.domain.user.consultant.controller;

import com.h5.domain.user.consultant.dto.request.RegisterParentAccount;
import com.h5.domain.user.consultant.dto.response.GetChildResponse;
import com.h5.domain.user.consultant.dto.response.GetMyChildrenResponse;
import com.h5.domain.user.consultant.dto.response.MyProfileResponse;
import com.h5.domain.user.consultant.dto.response.RegisterParentAccountResponse;
import com.h5.domain.user.consultant.service.ConsultantUserService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultants")
@RequiredArgsConstructor
@Tag(name = "Consultant API", description = "상담사 관련 기능")
public class ConsultantUserController {

    private final ConsultantUserService consultantUserService;

    @Operation(
            summary  = "현재 인증된 상담사 프로필 조회",
            description = "인증 토큰에 담긴 상담사 정보를 기반으로 프로필을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 토큰이 유효하지 않음")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/me/profile")
    public ResultResponse<MyProfileResponse> getMyProfile() {
        MyProfileResponse response = consultantUserService.getMyProfile();
        return ResultResponse.success(response);
    }

    @Operation(
            summary  = "학부모 계정 등록",
            description = "인증된 상담사가 새로운 학부모 계정을 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "학부모 계정 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "인증 토큰이 유효하지 않음")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @PostMapping("/me/parents")
    public ResultResponse<RegisterParentAccountResponse> registerParentAccount(
            @Parameter(description = "학부모 계정 등록 요청 DTO", required = true)
            @Valid @RequestBody RegisterParentAccount dto
    ) {
        return ResultResponse.created(consultantUserService.registerParentAccount(dto));
    }

    @Operation(
            summary  = "담당 자녀 목록 조회",
            description = "현재 인증된 상담사가 담당하는 자녀 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "담당 자녀 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 토큰이 유효하지 않음")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/me/children")
    public ResultResponse<List<GetMyChildrenResponse>> getMyChildren() {
        return ResultResponse.success(consultantUserService.getMyChildren());
    }

    @Operation(
            summary  = "특정 자녀 정보 조회",
            description = "자녀 ID(childUserId)를 경로 변수로 받아 해당 자녀 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자녀 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 자녀를 찾을 수 없음")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/children/{childUserId}")
    public ResultResponse<GetChildResponse> getChild(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @Valid @PathVariable int childUserId
    ) {
        return ResultResponse.success(consultantUserService.getChild(childUserId));
    }

}
