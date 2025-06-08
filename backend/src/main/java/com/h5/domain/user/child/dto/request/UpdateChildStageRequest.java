package com.h5.domain.user.child.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자녀 단계 업데이트 요청 DTO")
public class UpdateChildStageRequest {

    @NotNull(message = "chapter는 필수 입력 항목입니다.")
    @Min(value = 1, message = "chapter는 1 이상이어야 합니다.")
    @Schema(description = "업데이트할 챕터 번호", example = "1", required = true)
    private Integer chapter;

    @NotNull(message = "stage는 필수 입력 항목입니다.")
    @Min(value = 1, message = "stage는 1 이상이어야 합니다.")
    @Schema(description = "업데이트할 스테이지 번호", example = "3", required = true)
    private Integer stage;
}
