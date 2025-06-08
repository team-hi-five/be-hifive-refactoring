package com.h5.domain.file.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h5.domain.file.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "파일 업로드 응답 DTO")
public class FileResponse {

    @Schema(description = "저장된 파일의 경로", example = "IMAGE/123/abcd1234_myphoto.png")
    private final String filePath;

    @Schema(description = "원본 파일명", example = "myphoto.png")
    private final String originFileName;

    @Schema(description = "업로드 일시", example = "2025-06-09T14:23:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime uploadAt;

    @Schema(description = "파일이 속한 테이블 타입", example = "IMAGE")
    private final FileEntity.TblType tblType;

    @Schema(description = "파일이 속한 테이블의 ID", example = "123")
    private final Integer tblId;
}
