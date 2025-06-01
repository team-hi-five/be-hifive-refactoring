package com.h5.domain.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알람 정보 DTO")
public class AlarmDto {

    @Schema(description = "알람 메시지 내용", example = "새로운 메시지가 도착했습니다.")
    private String message;

    @Schema(description = "알람을 받을 사용자 이메일", example = "user@example.com")
    private String toUserEmail;

    @Schema(description = "알람 생성 시각", example = "2025-06-01T14:30:00")
    private LocalDateTime time;
}
