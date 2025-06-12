package com.h5.domain.session.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "회의 참여 또는 생성 결과를 담은 응답 정보")
@Getter
@AllArgsConstructor
@Builder
public class JoinSessionResponse {

    @Schema(description = "생성되거나 참여된 세션의 OpenVidu 세션 ID", example = "session-abc123")
    private final String sessionId;
}
