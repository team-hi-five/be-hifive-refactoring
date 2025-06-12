package com.h5.domain.file.dto.request;

import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.entity.TblType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "파일 업로드 요청 DTO")
public class FileUploadRequest {

    @NotEmpty(message = "tblType 목록은 하나 이상 필요합니다.")
    @Schema(
            description = "업로드할 파일이 속한 테이블 타입 리스트",
            example = "[IMAGE, DOCUMENT]"
    )
    private List<TblType> tblType;

    @NotEmpty(message = "tblId 목록은 하나 이상 필요합니다.")
    @Schema(
            description = "업로드할 파일이 속한 테이블 ID 리스트",
            example = "[123, 456]"
    )
    private List<Integer> tblId;
}
