package com.h5.domain.deleterequest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h5.domain.deleterequest.entity.DeleteUserRequestEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "삭제 요청 승인 응답 DTO")
public class DeleteUserRequestAprproveResponseDto {

    @Schema(description = "삭제 요청 ID", example = "1")
    private final int deleteRequestId;

    @Schema(description = "삭제 요청 상태", example = "A")
    private final DeleteUserRequestEntity.Status status;

    @Schema(description = "삭제 요청 승인 일자 (yyyy-MM-dd 형식)", example = "2025-06-04")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime deleteConfirmDttm;
}
