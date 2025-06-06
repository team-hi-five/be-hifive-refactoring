package com.h5.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class GetUserRoleResponseDto {

    @Schema(description = "이름", example = "정찬환")
    private final String name;

    @Schema(description = "권한", example = "PARENT")
    private final String role;
}
