package com.h5.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(description = "사용자 이름", example = "정찬환")
    private String name;

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "비밀번호 변경 여부", example = "false")
    private boolean pwdChanged;
}
