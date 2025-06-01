package com.h5.domain.chatbot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "챗봇 대화 날짜 목록 응답 DTO")
public class GetChatbotDatesResponseDto {

    @Schema(
            description = "챗봇 대화 날짜 목록",
            example = "[\"2025-06-01\",\"2025-06-02\",\"2025-06-03\"]"
    )
    private List<LocalDate> dateList;
}
