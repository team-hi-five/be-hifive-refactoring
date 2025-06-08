package com.h5.domain.user.consultant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "이메일 중복 확인 응답 DTO")
public class EmailCheckResponse {

    @Schema(description = "이미 계정이 존재하는지 여부", example = "true")
    private final Boolean alreadyAccount;

    @Schema(description = "이메일 주소", example = "parent@example.com")
    private final String email;

    @Schema(description = "부모 이름", example = "김철수")
    private final String parentName;

    @Schema(description = "부모 전화번호", example = "010-1234-5678")
    private final String parentPhone;
}
