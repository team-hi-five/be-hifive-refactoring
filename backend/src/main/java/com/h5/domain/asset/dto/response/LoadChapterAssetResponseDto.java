package com.h5.domain.asset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class LoadChapterAssetResponseDto {

    @Schema(description = "사용 가능한 챕터 목록",
            example = "[{\"gameChapterId\":1,\"title\":\"첫 번째 챕터\",\"chapterPic\":\"https://example.com/images/chapter1.png\"}, {\"gameChapterId\":2,\"title\":\"두 번째 챕터\",\"chapterPic\":\"https://example.com/images/chapter2.png\"}]")
    private final List<ChapterAssetResponseDto> chapterAssetDtoList;

    @Schema(description = "자녀가 접근 가능한 챕터 한도", example = "3")
    private final Integer limit;
}
