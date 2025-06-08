package com.h5.domain.user.consultant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "부모 계정 및 자녀 계정 등록 응답 DTO")
public class RegisterParentAccountResponse {

    @Schema(description = "등록된 부모 사용자 ID", example = "1001")
    private final Integer parentUserId;

    @Schema(description = "등록된 아동 사용자 ID", example = "2001")
    private final Integer childUserId;
}
