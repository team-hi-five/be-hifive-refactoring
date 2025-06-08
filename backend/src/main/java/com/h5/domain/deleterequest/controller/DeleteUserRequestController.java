package com.h5.domain.deleterequest.controller;

import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.deleterequest.dto.response.DeleteRequestResponse;
import com.h5.domain.deleterequest.dto.response.DeleteUserRequestAprproveResponse;
import com.h5.domain.deleterequest.dto.response.DeleteUserRequestRejectResponse;
import com.h5.domain.deleterequest.dto.response.GetMyDeleteResponse;
import com.h5.domain.deleterequest.service.DeleteUserRequestService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delete-requests")
@Tag(name = "Delete User Request API", description = "회원 삭제 요청 관련 API")
public class DeleteUserRequestController {

    private final DeleteUserRequestService deleteUserRequestService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "삭제 요청 생성",
            description = "부모 사용자가 탈퇴 요청을 생성합니다."
    )
    @PostMapping
    public ResultResponse<DeleteRequestResponse> deleteRequest() {
        String parentEmail = authenticationService.getCurrentUserEmail();
        return ResultResponse.success(deleteUserRequestService.deleteRequest(parentEmail));
    }

    @Operation(
            summary = "삭제 요청 승인",
            description = "상담사가 지정된 탈퇴 요청을 승인합니다."
    )
    @PatchMapping("/{deleteUserRequestId}/approve")
    public ResultResponse<DeleteUserRequestAprproveResponse> approve(
            @Parameter(description = "승인할 탈퇴 요청 ID", required = true)
            @PathVariable("deleteUserRequestId") @Valid Integer deleteUserRequestId
    ) {
        deleteUserRequestService.deleteApprove(deleteUserRequestId);
        return ResultResponse.success(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "삭제 요청 거절",
            description = "상담사가 지정된 탈퇴 요청을 거절합니다."
    )
    @PatchMapping("/{deleteUserRequestId}/reject")
    public ResultResponse<DeleteUserRequestRejectResponse> reject(
            @Parameter(description = "거절할 탈퇴 요청 ID", required = true)
            @PathVariable("deleteUserRequestId") @Valid Integer deleteUserRequestId
    ) {
        deleteUserRequestService.deleteReject(deleteUserRequestId);
        return ResultResponse.success(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "내가 받은 탈퇴 요청 조회",
            description = "상담사가 자신에게 접수된 탈퇴 요청 목록을 조회합니다."
    )
    @GetMapping
    public ResultResponse<List<GetMyDeleteResponse>> getMyDelete() {
        String consultantEmail = authenticationService.getCurrentUserEmail();
        return ResultResponse.success(deleteUserRequestService.getMyDelete(consultantEmail));
    }
}
