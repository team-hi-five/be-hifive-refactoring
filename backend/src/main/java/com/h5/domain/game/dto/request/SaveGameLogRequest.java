package com.h5.domain.game.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "게임 로그 저장 요청 DTO")
public class SaveGameLogRequest {

    @NotNull(message = "selectedOption은 필수 값입니다.")
    @Schema(description = "사용자가 선택한 옵션 번호", example = "2")
    private Integer selectedOption;

    @NotNull(message = "corrected은 필수 값입니다.")
    @Schema(description = "사용자가 정답을 맞추었는지 여부", example = "true")
    private Boolean corrected;

    @NotNull(message = "consulted은 필수 값입니다.")
    @Schema(description = "상담 요청 여부", example = "false")
    private Boolean consulted;

    @NotNull(message = "childGameStageId는 필수 값입니다.")
    @Schema(description = "어린이 게임 스테이지 ID", example = "3")
    private Integer childGameStageId;

    @NotNull(message = "childUserId는 필수 값입니다.")
    @Schema(description = "어린이 사용자 ID", example = "5")
    private Integer childUserId;

    @NotNull(message = "gameStageId는 필수 값입니다.")
    @Schema(description = "게임 스테이지 ID", example = "7")
    private Integer gameStageId;

    @NotNull(message = "fHappy는 필수 값입니다.")
    @Schema(description = "게임 전 얼굴 감정 – 행복도", example = "10")
    private Integer fHappy;

    @NotNull(message = "fAnger는 필수 값입니다.")
    @Schema(description = "게임 전 얼굴 감정 – 분노도", example = "2")
    private Integer fAnger;

    @NotNull(message = "fSad는 필수 값입니다.")
    @Schema(description = "게임 전 얼굴 감정 – 슬픔도", example = "3")
    private Integer fSad;

    @NotNull(message = "fPanic는 필수 값입니다.")
    @Schema(description = "게임 전 얼굴 감정 – 공포도", example = "1")
    private Integer fPanic;

    @NotNull(message = "fFear는 필수 값입니다.")
    @Schema(description = "게임 전 얼굴 감정 – 두려움도", example = "4")
    private Integer fFear;

    @NotNull(message = "tHappy는 필수 값입니다.")
    @Schema(description = "게임 후 텍스트 감정 – 행복도", example = "12")
    private Integer tHappy;

    @NotNull(message = "tAnger는 필수 값입니다.")
    @Schema(description = "게임 후 텍스트 감정 – 분노도", example = "1")
    private Integer tAnger;

    @NotNull(message = "tSad는 필수 값입니다.")
    @Schema(description = "게임 후 텍스트 감정 – 슬픔도", example = "2")
    private Integer tSad;

    @NotNull(message = "tPanic는 필수 값입니다.")
    @Schema(description = "게임 후 텍스트 감정 – 공포도", example = "0")
    private Integer tPanic;

    @NotNull(message = "tFear는 필수 값입니다.")
    @Schema(description = "게임 후 텍스트 감정 – 두려움도", example = "3")
    private Integer tFear;

    @NotBlank(message = "stt는 필수 값입니다.")
    @Schema(description = "음성 인식 텍스트(STT)", example = "오늘 게임이 정말 재미있었어요!")
    private String stt;

    @NotBlank(message = "aiAnalysis는 필수 값입니다.")
    @Schema(description = "AI 분석 결과", example = "아이의 반응이 매우 긍정적이며 집중도가 높았습니다.")
    private String aiAnalysis;
}
