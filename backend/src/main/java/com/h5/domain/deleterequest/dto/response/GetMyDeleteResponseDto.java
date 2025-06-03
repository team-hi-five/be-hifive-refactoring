package com.h5.domain.deleterequest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "내 삭제 요청 조회 응답 DTO")
public class GetMyDeleteResponseDto {

    @Schema(description = "삭제 요청 ID", example = "1")
    private final Integer deleteUserRequestId;

    @Schema(description = "부모 사용자 ID", example = "42")
    private final Integer parentUserId;

    @Schema(description = "부모 이름", example = "김철수")
    private final String parentName;

    @Schema(description = "삭제 요청 일자 (yyyy-MM-dd HH:mm:ss 형식)", example = "2025-06-04 15:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime deleteRequestDttm;

    @Schema(description = "삭제 요청에 포함된 자녀 정보 목록")
    private final Set<GetMyDeleteChildResponseDto> children;
}
