package com.h5.domain.parent.controller;

import com.h5.domain.consultant.dto.request.FindEmailRequestDto;
import com.h5.domain.consultant.dto.request.UpdatePwdRequestDto;
import com.h5.domain.consultant.dto.request.UpdateToTempPwdRequestDto;
import com.h5.domain.consultant.dto.response.EmailCheckResponse;
import com.h5.domain.parent.dto.response.MyPageResponseDto;
import com.h5.domain.parent.service.ParentUserService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/parent")
public class ParentUserController {

    private final ParentUserService parentUserService;

    @Autowired
    public ParentUserController(ParentUserService parentUserService) {
        this.parentUserService = parentUserService;
    }

    @PostMapping("/my-page")
    public MyPageResponseDto myPage() {
        return parentUserService.getMyPageInfo();
    }

    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@Valid @RequestBody FindEmailRequestDto findEmailRequestDto) {
        String email = parentUserService.findId(findEmailRequestDto.getName(), findEmailRequestDto.getPhone()).getEmail();
        return ResponseEntity.ok(email);
    }

    @PostMapping("/temp-pwd")
    public ResponseEntity<String> updateToTempPwd(@Valid @RequestBody UpdateToTempPwdRequestDto updateToTempPwdRequestDto) {
        parentUserService.updateToTempPwd(updateToTempPwdRequestDto.getEmail());
        return ResponseEntity.ok("Temporary password sent successfully and updated.");
    }

    @PostMapping("/change-pwd")
    public ResponseEntity<?> updatePwd(@Valid @RequestBody UpdatePwdRequestDto updatePwdRequestDto) {
        parentUserService.updatePwd(updatePwdRequestDto.getOldPwd(),
                updatePwdRequestDto.getNewPwd());
        return ResponseEntity.ok("Password changed successfully.");
    }
    
    @GetMapping("/my-children")
    public ResponseEntity<?> myChildren() {
        return ResponseEntity.ok(parentUserService.myChildren());
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
        return ResultResponse.success(parentUserService.emailCheck(email));
    }

}
