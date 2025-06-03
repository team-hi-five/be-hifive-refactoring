package com.h5.domain.child.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아동 검색 응답 DTO")
public class SearchChildResponse {

    @Schema(description = "아동 사용자 ID", example = "101")
    private final Integer childUserId;

    @Schema(description = "아동 프로필 이미지 URL", example = "https://example.com/childProfile.jpg")
    private final String childProfileUrl;

    @Schema(description = "아동 이름", example = "김영희")
    private final String childUserName;

    @Schema(description = "부모 이름", example = "김철수")
    private final String parentUserName;

    @Schema(description = "부모 이메일", example = "parent@example.com")
    private final String parentUserEmail;
}
