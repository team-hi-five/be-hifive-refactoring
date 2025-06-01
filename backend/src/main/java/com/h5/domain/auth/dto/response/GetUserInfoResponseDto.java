package com.h5.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class GetUserInfoResponseDto {

    @Schema(description = "이름", example = "정찬환")
    private String name;

    @Schema(description = "권한", example = "PARENT")
    private String role;
}
