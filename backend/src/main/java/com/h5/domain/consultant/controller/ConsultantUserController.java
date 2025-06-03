package com.h5.domain.consultant.controller;

import com.h5.domain.child.service.ChildUserService;
import com.h5.domain.consultant.dto.request.*;
import com.h5.domain.consultant.dto.response.*;
import com.h5.domain.consultant.service.ConsultantUserService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultants")
@RequiredArgsConstructor
@Tag(name = "Consultant API", description = "상담사 관련 기능")
public class ConsultantUserController {

    private final ConsultantUserService consultantUserService;

    @Operation(
            summary  = "이름과 전화번호로 상담사 이메일 조회",
            description = "이름(name)과 전화번호(phone) 쿼리 파라미터를 사용해 상담사 이메일을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상담사를 찾을 수 없음")
    })
    @GetMapping("/email")
    public ResultResponse<GetEmailResponse> findEmail(
            @Parameter(description = "조회할 상담사 이름", example = "홍길동")
            @Valid @RequestParam String name,
            @Parameter(description = "조회할 상담사 전화번호", example = "01012345678")
            @Valid @RequestParam String phone
    ) {
        return ResultResponse.success(consultantUserService.findEmail(name, phone));
    }

    @Operation(
            summary  = "임시 비밀번호 발급",
            description = "이름(name)과 이메일(email)을 바디에 담아 요청하면 임시 비밀번호를 발급하고 메일로 전송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 비밀번호 발급 및 전송 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상담사 계정을 찾을 수 없음")
    })
    @PostMapping("/password/temporary")
    public ResultResponse<Void> updateToTempPwd(
            @Parameter(description = "임시 비밀번호 발급 요청 DTO", required = true)
            @Valid @RequestBody UpdateToTempPwdRequestDto dto
    ) {
        consultantUserService.issueTemporaryPassword(dto.getEmail());
        return ResultResponse.success();
    }

    @Operation(
            summary  = "비밀번호 변경",
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
            @Parameter(description = "비밀번호 변경 요청 DTO", required = true)
            @Valid @RequestBody UpdatePwdRequestDto dto
    ) {
        consultantUserService.updatePwd(dto);
        return ResultResponse.success();
    }

    @Operation(
            summary  = "현재 인증된 상담사 프로필 조회",
            description = "인증 토큰에 담긴 상담사 정보를 기반으로 프로필을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 토큰이 유효하지 않음")
    })
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
    @PostMapping("/me/parents")
    public ResultResponse<RegisterParentAccountResponse> registerParentAccount(
            @Parameter(description = "학부모 계정 등록 요청 DTO", required = true)
            @Valid @RequestBody RegisterParentAccountDto dto
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
    @GetMapping("/children/{childUserId}")
    public ResultResponse<GetChildResponse> getChild(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @Valid @PathVariable int childUserId
    ) {
        return ResultResponse.success(consultantUserService.getChild(childUserId));
    }

}
