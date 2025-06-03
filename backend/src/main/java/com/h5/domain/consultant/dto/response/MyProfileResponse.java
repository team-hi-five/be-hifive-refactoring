package com.h5.domain.consultant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "내 프로필 조회 응답 DTO")
public class MyProfileResponse {

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImgUrl;

    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private final String phone;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "센터 이름", example = "행복한 돌봄 센터")
    private final String centerName;

    @Schema(description = "센터 전화번호", example = "02-987-6543")
    private final String centerPhone;
}
