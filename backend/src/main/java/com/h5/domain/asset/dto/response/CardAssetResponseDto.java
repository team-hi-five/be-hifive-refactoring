package com.h5.domain.asset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "카드 자산 정보 DTO")
public class CardAssetResponseDto {

    @Schema(description = "해당 카드의 스테이지 ID", example = "3")
    private int stageId;

    @Schema(description = "카드 앞면 이미지 URL", example = "https://example.com/images/card_front.png")
    private String cardFront;

    @Schema(description = "카드 뒷면 이미지 URL", example = "https://example.com/images/card_back.png")
    private String cardBack;
}
