package com.h5.domain.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "파일 업로드 결과 응답 DTO")
public class FileUploadResponse {

    @Schema(description = "업로드된 파일 정보 리스트")
    private final List<FileResponse> files;
}
