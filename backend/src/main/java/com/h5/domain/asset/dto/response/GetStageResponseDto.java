package com.h5.domain.asset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetStageResponseDto {

    @Schema(description = "현재 클리어된 챕터 번호", example = "2")
    private final Integer chapter;

    @Schema(description = "현재 클리어된 스테이지 번호", example = "3")
    private final Integer stage;
}
