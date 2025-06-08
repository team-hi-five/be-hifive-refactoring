package com.h5.domain.auth.controller;

import com.h5.domain.auth.dto.request.LoginRequestDto;
import com.h5.domain.auth.dto.response.GetUserRoleResponseDto;
import com.h5.domain.auth.dto.response.LoginResponseDto;
import com.h5.domain.auth.dto.response.RefreshAccessTokenResponseDto;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.user.consultant.dto.request.UpdatePwdRequestDto;
import com.h5.domain.user.consultant.dto.request.UpdateToTempPwdRequestDto;
import com.h5.domain.user.consultant.dto.response.GetEmailResponse;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Auth API", description = "인증 관련 API")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

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
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보 (이메일, 비밀번호, 역할)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
            )
            @Valid @RequestBody LoginRequestDto loginRequestDto
    ) {
        return ResultResponse.success(authenticationService.authenticateAndGenerateToken(loginRequestDto));
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
        authenticationService.logout();
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
        return ResultResponse.success(authenticationService.refreshAccessToken());
    }

    @Operation(
            summary = "현재 사용자 권한 조회",
            description = "헤더의 액세스 토큰을 기반으로 현재 인증된 사용자의 권한을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 액세스 토큰")
    })
    @GetMapping("/role")
    public ResultResponse<GetUserRoleResponseDto> getUserRole() {
        return ResultResponse.success(authenticationService.getUserInfo());
    }

    @Operation(
            summary = "이름과 전화번호, 역할로 이메일 조회",
            description = "이름(name)과 전화번호(phone) 역할(role) 쿼리 파라미터를 사용해 이메일을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상담사를 찾을 수 없음")
    })
    @GetMapping("/email")
    public ResultResponse<GetEmailResponse> findEmail(
            @Parameter(description = "조회할 이름", example = "홍길동")
            @Valid @RequestParam String name,
            @Parameter(description = "조회할 전화번호", example = "01012345678")
            @Valid @RequestParam String phone,
            @Parameter(description = "역할", example = "consultant")
            @Valid @RequestParam String role
    ) {
        return ResultResponse.success(authenticationService.findEmail(name, phone, role));
    }

    @Operation(
            summary = "임시 비밀번호 발급",
            description = "이름(name)과 이메일(email)을 바디에 담아 요청하면 임시 비밀번호를 발급하고 메일로 전송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 비밀번호 발급 및 전송 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상담사 계정을 찾을 수 없음")
    })
    @PostMapping("/password/temporary")
    public ResultResponse<Void> updateToTempPwd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "임시 비밀번호 발급 요청 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateToTempPwdRequestDto.class))
            )
            @Valid @RequestBody UpdateToTempPwdRequestDto dto
    ) {
        authenticationService.issueTemporaryPassword(dto);
        return ResultResponse.success();
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "기존 비밀번호(oldPwd)와 새 비밀번호(newPwd)를 전송하여 비밀번호를 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "기존 비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "상담사 계정을 찾을 수 없음")
    })
    @PutMapping("/password")
    public ResultResponse<Void> updatePwd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 변경 요청 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePwdRequestDto.class))
            )
            @Valid @RequestBody UpdatePwdRequestDto dto
    ) {
        authenticationService.updatePwd(dto);
        return ResultResponse.success();
    }
}
