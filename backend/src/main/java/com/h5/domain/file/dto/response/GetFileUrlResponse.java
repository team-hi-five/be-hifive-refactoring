package com.h5.domain.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "파일 URL 조회 응답 DTO")
public class GetFileUrlResponse {

    @Schema(description = "파일 ID", example = "1")
    private final Integer fileId;

    @Schema(description = "파일 다운로드 또는 접근용 URL", example = "https://api.example.com/files/1/download")
    private final String url;

    @Schema(description = "원본 파일명", example = "myphoto.png")
    private final String fileName;
}
