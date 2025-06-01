package com.h5.domain.chatbot.dto.request;

import com.h5.domain.chatbot.document.ChatBotDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "챗봇 대화 저장 요청 DTO")
public class InsertChatbotRequestDto {

    @Schema(description = "저장할 챗봇 대화 문서 목록")
    @NotNull
    @NotEmpty
    private List<ChatbotMessageDto> chatbotMessageDtoList;

}
