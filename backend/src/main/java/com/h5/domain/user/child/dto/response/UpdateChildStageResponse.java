package com.h5.domain.user.child.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "자녀 단계 업데이트 결과 DTO")
public class UpdateChildStageResponse {

    @Schema(description = "갱신된 전체 클리어 단계. 이미 해당 단계 이상을 클리어한 경우 0이 반환됩니다.", example = "7")
    private final Integer stage;
}
