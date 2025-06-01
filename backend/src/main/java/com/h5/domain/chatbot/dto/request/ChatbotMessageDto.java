package com.h5.domain.chatbot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "챗봇 대화 메시지 정보 DTO")
public class ChatbotMessageDto {

    @Schema(description = "자녀 사용자 ID", example = "123")
    @NotNull
    private Integer childUserId;

    @Schema(description = "메시지 발신자 (USER or BOT)", example = "USER")
    @NotNull
    @Size(min = 1)
    private String sender;

    @Schema(description = "메시지 순서 인덱스", example = "1")
    @NotNull
    @Min(0)
    private Integer messageIndex;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    @NotNull
    @Size(min = 1)
    private String message;
}
