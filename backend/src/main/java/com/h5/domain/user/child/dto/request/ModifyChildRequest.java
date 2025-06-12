package com.h5.domain.user.child.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "아동 정보 수정 요청 DTO")
public class ModifyChildRequest {

    @Schema(description = "아동의 관심사", example = "축구")
    private String interest;

    @Schema(description = "추가 정보", example = "특별 지원 필요 없음")
    private String additionalInfo;
}
