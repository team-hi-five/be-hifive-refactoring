package com.h5.domain.user.consultant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class UpdatePwdRequestDto {

    @Schema(description = "현재 비밀번호", example = "oldPassword123!")
    private String oldPwd;

    @Schema(description = "새 비밀번호", example = "newPassword456!")
    private String newPwd;
}
