package com.h5.domain.asset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoadAssetResponseDto {

    @Schema(description = "게임 스테이지 ID", example = "7")
    private int gameStageId;

    @Schema(description = "챕터 ID", example = "2")
    private int chapterId;

    @Schema(description = "게임 비디오 URL", example = "https://example.com/videos/game7.mp4")
    private String gameVideo;

    @Schema(description = "정답 보기 옵션 목록", example = "[\"option1\",\"option2\",\"option3\"]")
    private String[] options;

    @Schema(description = "옵션 이미지 URL 목록", example = "[\"https://example.com/images/opt1.png\",\"https://example.com/images/opt2.png\",\"https://example.com/images/opt3.png\"]")
    private String[] optionImages;

    @Schema(description = "상황 설명 텍스트", example = "주인공이 숲 속을 탐험하고 있습니다.")
    private String situation;

    @Schema(description = "정답 인덱스", example = "2")
    private int answer;

    @Schema(description = "카드 앞면 이미지 URL", example = "https://example.com/images/card_front7.png")
    private String cardFront;

    @Schema(description = "카드 뒷면 이미지 URL", example = "https://example.com/images/card_back7.png")
    private String cardBack;
}
