package com.h5.domain.statistic.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아동 사용자의 감정별 통계 분석 결과 DTO")
public class DataAnalysisResponse {

    @Schema(description = "아동 사용자 ID", example = "123")
    private final Integer childUserId;

    @Schema(description = "아동 사용자 이름", example = "홍길동")
    private final String childName;

    @Schema(description = "감정 ID", example = "2")
    private final Integer emotionId;

    @Schema(description = "감정별 평점", example = "85")
    private final Integer rating;

    @Schema(description = "전체 시도 횟수", example = "50")
    private final Integer totalTryCnt;

    @Schema(description = "전체 정답 횟수", example = "40")
    private final Integer totalCrtCnt;

    @Schema(description = "단계별 정답 비율(1~5단계)",
            example = "[\"0.80\",\"0.75\",\"0.90\",\"0.85\",\"0.60\"]")
    private final List<BigDecimal> stageCrtRate;

    @Schema(description = "단계별 시도 횟수(1~5단계)", example = "[10,10,10,10,10]")
    private final List<Integer> stageTryCnt;

    @Schema(description = "단계별 정답 횟수(1~5단계)", example = "[8,7,9,8,6]")
    private final List<Integer> stageCrtCnt;
}
