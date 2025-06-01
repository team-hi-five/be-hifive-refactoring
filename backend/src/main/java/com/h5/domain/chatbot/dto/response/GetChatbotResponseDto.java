package com.h5.domain.chatbot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "챗봇 대화 내역 응답 DTO")
public class GetChatbotResponseDto {

    @Schema(description = "해당 날짜의 챗봇 메시지 목록")
    private final List<ChatbotMessageResponseDto> chatBotMessageList;
}
