package com.h5.domain.user.parent.controller;

import com.h5.domain.user.consultant.dto.response.EmailCheckResponse;
import com.h5.domain.user.parent.dto.response.MyChildrenResponse;
import com.h5.domain.user.parent.dto.response.MyPageResponse;
import com.h5.domain.user.parent.service.ParentUserService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parents")
public class ParentUserController {

    private final ParentUserService parentUserService;

    @GetMapping()
    public ResultResponse<MyPageResponse> myPage() {
        return ResultResponse.success(parentUserService.getMyPageInfo());
    }

    @Operation(
            summary  = "자녀 목록 조회",
            description = "현재 인증된 부모의 자녀 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자녀 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 토큰이 유효하지 않음")
    })
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @GetMapping("/me/children")
    public ResultResponse<List<MyChildrenResponse>> getMyChildren() {
        return ResultResponse.success(parentUserService.getMyChildren());
    }

    @Operation(
            summary  = "이메일 중복 여부 확인",
            description = "쿼리 파라미터로 전달된 이메일의 계정 존재 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계정 존재 여부 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식")
    })
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    @GetMapping("/search")
    public ResultResponse<EmailCheckResponse> searchByEmail(
            @Parameter(description = "계정 존재 여부를 확인할 이메일", example = "test@example.com")
            @Valid @RequestParam String email
    ) {
        return ResultResponse.success(parentUserService.searchByEmail(email));
    }

}
