package com.h5.domain.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "챗봇 메시지 응답 DTO")
public class ChatbotMessageResponse {

    @Schema(description = "자녀 사용자 ID", example = "123")
    private final Integer childUserId;

    @Schema(description = "챗봇 사용 시각", example = "2025-06-15T14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime chatBotUsedAt;

    @Schema(description = "메시지 발신자 (USER 또는 BOT)", example = "USER")
    private final String sender;

    @Schema(description = "메시지 순서 인덱스", example = "1")
    private final Integer messageIndex;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private final String message;
}
