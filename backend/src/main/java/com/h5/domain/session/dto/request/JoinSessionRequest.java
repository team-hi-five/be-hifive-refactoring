package com.h5.domain.session.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회의 참여 또는 생성을 위한 요청 정보")
public class JoinSessionRequest {

    @NotNull(message = "scheduleId는 필수입니다.")
    @Schema(description = "참여 또는 생성할 스케줄의 ID", example = "123", required = true)
    private Integer scheduleId;

    @NotBlank(message = "type은 필수입니다. 'game' 또는 'consult' 중 하나여야 합니다.")
    @Schema(description = "스케줄 유형 ('game' 또는 'consult')", example = "game", required = true)
    private String type;
}
