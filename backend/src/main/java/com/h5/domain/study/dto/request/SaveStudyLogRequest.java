package com.h5.domain.study.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "학습 로그 저장 요청 DTO")
public class SaveStudyLogRequest {

    @Schema(description = "비디오 감정: 행복", example = "0.80")
    @NotNull(message = "fHappy는 필수값입니다.")
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private BigDecimal fHappy;

    @Schema(description = "비디오 감정: 분노", example = "0.10")
    @NotNull(message = "fAnger는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal fAnger;

    @Schema(description = "비디오 감정: 슬픔", example = "0.05")
    @NotNull(message = "fSad는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal fSad;

    @Schema(description = "비디오 감정: 공황", example = "0.02")
    @NotNull(message = "fPanic는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal fPanic;

    @Schema(description = "비디오 감정: 두려움", example = "0.03")
    @NotNull(message = "fFear는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal fFear;

    @Schema(description = "텍스트 감정: 행복", example = "0.75")
    @NotNull(message = "tHappy는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal tHappy;

    @Schema(description = "텍스트 감정: 분노", example = "0.15")
    @NotNull(message = "tAnger는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal tAnger;

    @Schema(description = "텍스트 감정: 슬픔", example = "0.05")
    @NotNull(message = "tSad는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal tSad;

    @Schema(description = "텍스트 감정: 공황", example = "0.03")
    @NotNull(message = "tPanic는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal tPanic;

    @Schema(description = "텍스트 감정: 두려움", example = "0.02")
    @NotNull(message = "tFear는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal tFear;

    @Schema(description = "음성인식(STT) 결과 텍스트", example = "오늘 하루가 즐거웠어요")
    @NotNull(message = "stt는 필수값입니다.")
    @Size(min = 1, max = 500, message = "stt 길이는 1~500자여야 합니다.")
    private String stt;

    @Schema(description = "텍스트 유사도 점수", example = "0.88")
    @NotNull(message = "textSimilarity는 필수값입니다.")
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal textSimilarity;

    @Schema(description = "학습 스테이지 ID", example = "42")
    @NotNull(message = "childGameStageId는 필수값입니다.")
    private Integer childGameStageId;
}
