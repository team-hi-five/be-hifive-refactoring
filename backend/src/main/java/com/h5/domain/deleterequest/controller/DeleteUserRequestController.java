package com.h5.domain.deleterequest.controller;

import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.deleterequest.dto.response.DeleteRequestResponseDto;
import com.h5.domain.deleterequest.dto.response.DeleteUserRequestAprproveResponseDto;
import com.h5.domain.deleterequest.dto.response.DeleteUserRequestRejectResponseDto;
import com.h5.domain.deleterequest.dto.response.GetMyDeleteResponseDto;
import com.h5.domain.deleterequest.service.DeleteUserRequestService;
import com.h5.global.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delete-requests")
public class DeleteUserRequestController {

    private final DeleteUserRequestService deleteUserRequestService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResultResponse<DeleteRequestResponseDto> deleteRequest() {
        String parentEmail = authenticationService.getCurrentUserEmail();
        return ResultResponse.success(deleteUserRequestService.deleteRequest(parentEmail));
    }

    @PatchMapping("/{deleteUserRequestId}/approve")
    public ResultResponse<DeleteUserRequestAprproveResponseDto> approve(@Valid @PathVariable Integer deleteUserRequestId) {
        deleteUserRequestService.deleteApprove(deleteUserRequestId);
        return ResultResponse.success(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{deleteUserRequestId}/reject")
    public ResultResponse<DeleteUserRequestRejectResponseDto> reject(@Valid @PathVariable Integer deleteUserRequestId) {
        deleteUserRequestService.deleteReject(deleteUserRequestId);
        return ResultResponse.success(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResultResponse<List<GetMyDeleteResponseDto>> getMyDelete() {
        String consultantEmail = authenticationService.getCurrentUserEmail();
        return ResultResponse.success(deleteUserRequestService.getMyDelete(consultantEmail));
    }
}
