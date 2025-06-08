package com.h5.domain.board.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "NoticeSaveResponse", description = "공지사항 저장(등록/수정) 응답 DTO")
public class NoticeSaveResponse {

    @Schema(description = "저장된 공지사항의 ID", example = "123")
    private final Integer noticeId;
}
