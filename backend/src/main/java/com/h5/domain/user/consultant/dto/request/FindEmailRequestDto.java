package com.h5.domain.user.consultant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "상담사 이메일 조회 요청 DTO")
public class FindEmailRequestDto {

    @Schema(description = "상담사 이름", example = "홍길동")
    private String name;

    @Schema(description = "상담사 전화번호", example = "010-1234-5678")
    private String phone;
}
