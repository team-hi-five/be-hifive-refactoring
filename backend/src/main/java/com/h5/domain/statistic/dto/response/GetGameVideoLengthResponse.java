package com.h5.domain.statistic.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아동 사용자의 게임 영상 재생 시도 인덱스 및 로그 ID DTO")
public class GetGameVideoLengthResponse {

    /**
     * 해당 스테이지에서 몇 번째 시도인지 나타내는 인덱스 (0부터 시작)
     */
    @Schema(description = "시도 인덱스 (0부터 시작)", example = "0")
    private final Integer tryIndex;

    /**
     * 실제 게임 로그 엔티티의 ID
     */
    @Schema(description = "게임 로그 ID", example = "456")
    private final Integer gameLogId;
}
