package com.h5.domain.chatbot.controller;

import com.h5.domain.chatbot.dto.request.InsertChatbotRequestDto;
import com.h5.domain.chatbot.dto.response.GetChatbotDatesResponseDto;
import com.h5.domain.chatbot.dto.response.GetChatbotResponseDto;
import com.h5.domain.chatbot.service.ChatbotService;
import com.h5.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Chatbot API", description = "챗봇 관련 API")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Operation(
            summary = "챗봇 대화 저장",
            description = "사용자가 입력한 챗봇 대화를 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "챗봇 대화 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public ResultResponse<Void> insertChatbot(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "챗봇 대화 저장 요청 DTO",
                    required = true
            )
            @Valid @RequestBody InsertChatbotRequestDto insertChatbotRequestDto
    ) {
        chatbotService.insertChatbot(insertChatbotRequestDto);
        return ResultResponse.success();
    }

    @Operation(
            summary = "월별 챗봇 대화 가능 날짜 조회",
            description = "자녀 ID와 연도, 월을 받아 해당 월에 챗봇 대화를 할 수 있는 날짜 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/{childUserId}/dates")
    public ResultResponse<GetChatbotDatesResponseDto> getChatbotDates(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @PathVariable int childUserId,
            @Parameter(description = "조회할 연도", example = "2025")
            @RequestParam int year,
            @Parameter(description = "조회할 월", example = "6")
            @RequestParam int month
    ) {
        GetChatbotDatesResponseDto dto = chatbotService.getChatbotDates(childUserId, year, month);
        return ResultResponse.success(dto);
    }

    @Operation(
            summary = "특정 날짜 챗봇 대화 조회",
            description = "자녀 ID와 날짜를 받아 해당 날짜의 챗봇 대화를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/{childUserId}")
    public ResultResponse<GetChatbotResponseDto> getChatbot(
            @Parameter(description = "조회할 자녀 사용자 ID", example = "123")
            @PathVariable int childUserId,
            @Parameter(description = "조회할 날짜 (ISO 형식, yyyy-MM-dd)", example = "2025-06-15")
            @RequestParam @NotNull LocalDate date
    ) {
        GetChatbotResponseDto dto = chatbotService.getChatbot(childUserId, date);
        return ResultResponse.success(dto);
    }
}
