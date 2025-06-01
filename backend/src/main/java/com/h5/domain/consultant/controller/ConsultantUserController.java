package com.h5.domain.consultant.controller;

import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.child.service.ChildUserService;
import com.h5.domain.consultant.dto.request.*;
import com.h5.domain.consultant.dto.response.*;
import com.h5.domain.consultant.entity.ConsultantUserEntity;
import com.h5.domain.consultant.service.ConsultantUserService;
import com.h5.domain.parent.service.ParentUserService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultants")
@RequiredArgsConstructor
@Tag(name = "Consultant API", description = "상담사 관련 기능")
public class ConsultantUserController {

    private final AuthenticationService authenticationService;
    private final ConsultantUserService consultantUserService;
    private final ParentUserService parentUserService;
    private final ChildUserService childUserService;

    @Operation(
            summary  = "이름과 전화번호로 상담사 이메일 조회",
            description = "이름(name)과 전화번호(phone) 쿼리 파라미터를 사용해 상담사 이메일을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상담사를 찾을 수 없음")
    })
    @GetMapping("/email")
    public ResultResponse<GetEmailResponse> findId(
            @Parameter(description = "조회할 상담사 이름", example = "홍길동")
            @Valid @RequestParam String name,
            @Parameter(description = "조회할 상담사 전화번호", example = "01012345678")
            @Valid @RequestParam String phone
    ) {
        GetEmailResponse response = consultantUserService.findEmail(name, phone);
        return ResultResponse.success(response);
    }

    @Operation(
            summary  = "이메일 중복 여부 확인",
            description = "쿼리 파라미터로 전달된 이메일의 중복 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "중복 여부 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식")
    })
    @GetMapping("/email-check")
    public ResultResponse<EmailCheckResponse> emailExists(
            @Parameter(description = "중복 여부를 확인할 이메일", example = "test@example.com")
            @Valid @RequestParam String email
    ) {
        EmailCheckResponse response = parentUserService.emailCheck(email);
        return ResultResponse.success(response);
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
        consultantUserService.issueTemporaryPassword(dto.getName(), dto.getEmail());
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
        String email = authenticationService.getCurrentUserEmail();
        consultantUserService.updatePwd(email, dto.getOldPwd(), dto.getNewPwd());
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
        String email = authenticationService.getCurrentUserEmail();
        MyProfileResponse response = consultantUserService.getProfile(email);
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
        String consultantEmail = authenticationService.getCurrentUserEmail();
        ConsultantUserEntity consultant = consultantUserService.findByEmailOrThrow(consultantEmail);

        // 부모 계정 생성 또는 조회
        var parent = parentUserService.createOrGetParent(dto, consultant);
        // 자녀 생성
        var child  = childUserService.createChild(dto, parent, consultant);

        RegisterParentAccountResponse responseDto =
                RegisterParentAccountResponse.builder()
                        .parentUserId(parent.getId())
                        .childUserId(child.getId())
                        .build();

        return ResultResponse.created(responseDto);
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
        String consultantEmail = authenticationService.getCurrentUserEmail();
        Integer consultantId = consultantUserService.findByEmailOrThrow(consultantEmail);

        List<GetMyChildrenResponse> responseList =
                childUserService.getChildrenByConsultant(consultant.getId());

        return ResultResponse.success(responseList);
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
        String consultantEmail = authenticationService.getCurrentUserEmail();
        ConsultantUserEntity consultant = consultantUserService.findByEmailOrThrow(consultantEmail);

        GetChildResponse response = childUserService.getChildDetail(childUserId, consultant.getId());
        return ResultResponse.success(response);
    }

    @Operation(
            summary  = "특정 자녀 정보 수정",
            description = "자녀 ID(childUserId)와 수정할 정보를 담은 DTO를 받아 자녀 정보를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자녀 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터가 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "해당 자녀를 찾을 수 없음")
    })
    @PutMapping("/children/{childUserId}")
    public ResultResponse<ModifyChildResponse> modifyChild(
            @Parameter(description = "수정할 자녀 사용자 ID", example = "123")
            @Valid @PathVariable int childUserId,
            @Parameter(description = "자녀 정보 수정 요청 DTO", required = true)
            @Valid @RequestBody ModifyChildRequestDto dto
    ) {
        dto.setChildUserId(childUserId);
        ModifyChildResponse response = childUserService.updateChild(dto);
        return ResultResponse.success(response);
    }

    @Operation(
            summary  = "자녀 이름으로 검색",
            description = "쿼리 파라미터(name)로 전달된 자녀 이름을 포함한 자녀 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자녀 검색 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터가 유효하지 않음")
    })
    @GetMapping("/children/search")
    public ResultResponse<List<SearchChildResponse>> searchChild(
            @Parameter(description = "검색할 자녀 이름", example = "김철수")
            @Valid @RequestParam String name
    ) {
        List<SearchChildResponse> responseList = childUserService.searchChildByName(name);
        return ResultResponse.success(responseList);
    }
}
