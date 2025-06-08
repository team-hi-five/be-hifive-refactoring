package com.h5.domain.user.consultant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "이메일 조회 응답 DTO")
public class GetEmailResponse {

    @Schema(description = "이메일 주소", example = "user@example.com")
    private final String email;
}
