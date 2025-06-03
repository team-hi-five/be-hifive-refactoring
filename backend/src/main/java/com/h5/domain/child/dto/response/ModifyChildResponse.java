package com.h5.domain.child.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아동 정보 수정 응답 DTO")
public class ModifyChildResponse {

    @Schema(description = "수정된 아동 사용자 ID", example = "123")
    private final Integer childUserId;
}
