package com.h5.domain.asset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChapterAssetResponseDto {

    @Schema(description = "게임 챕터 ID", example = "1")
    private int gameChapterId;

    @Schema(description = "챕터 제목", example = "첫 번째 챕터")
    private String title;

    @Schema(description = "챕터 이미지 URL", example = "https://example.com/images/chapter1.png")
    private String chapterPic;
}
