package com.h5.domain.alarm.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "알람 요청 DTO")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AlarmRequestDto {

    @Schema(description = "알람을 받을 사용자 ID", example = "123")
    private int toUserId;

    @Schema(description = "보내는 사람의 역할", example = "ROLE_CONSULTANT")
    private String senderRole;

    @Schema(description = "세션 타입", example = "CHAT")
    private String sessionType;
}
