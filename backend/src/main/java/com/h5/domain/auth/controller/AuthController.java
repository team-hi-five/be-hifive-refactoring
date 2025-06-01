package com.h5.domain.auth.controller;

import com.h5.domain.auth.dto.request.LoginRequestDto;
import com.h5.domain.auth.dto.response.GetUserInfoResponseDto;
import com.h5.domain.auth.dto.response.LoginResponseDto;
import com.h5.domain.auth.dto.response.RefreshAccessTokenResponseDto;
import com.h5.domain.auth.service.AuthService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;


    @Operation(
            summary = "로그인",
            description = "이메일, 비밀번호, 역할을 받아 인증 후 액세스/리프레시 토큰과 사용자 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 불일치)")
    })
    @PostMapping("/login")
    public ResultResponse<LoginResponseDto> login(
            @RequestBody(
                    description = "로그인 요청 정보 (이메일, 비밀번호, 역할)",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody LoginRequestDto loginRequestDto
    ) {
        return ResultResponse.success(authService.authenticateAndGenerateToken(loginRequestDto));
    }

    @Operation(
            summary = "로그아웃",
            description = "현재 인증된 사용자를 로그아웃 처리합니다. 액세스 토큰은 블랙리스트에 등록되고, 리프레시 토큰은 삭제됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "유효한 액세스 토큰이 없음")
    })
    @PostMapping("/logout")
    public ResultResponse<Void> logout() {
        authService.logout();
        return ResultResponse.success();
    }

    @Operation(
            summary = "액세스 토큰 재발급",
            description = "현재 인증된 사용자의 리프레시 토큰을 검증하여 새로운 액세스 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 만료되었거나 없는 경우")
    })
    @PostMapping("/refresh")
    public ResultResponse<RefreshAccessTokenResponseDto> refresh() {
        return ResultResponse.success(authService.refreshAccessToken());
    }

    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "헤더의 액세스 토큰을 기반으로 현재 인증된 사용자의 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 액세스 토큰")
    })
    @GetMapping
    public ResultResponse<GetUserInfoResponseDto> getUserInfo() {
        return ResultResponse.success(authService.getUserInfo());
    }
}
