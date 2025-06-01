package com.h5.domain.chatbot.controller;

import com.h5.domain.chatbot.dto.request.InsertChatbotRequestDto;
import com.h5.domain.chatbot.service.ChatbotService;
import com.h5.global.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/save")
    public ResultResponse<?> insertChatbot(@Valid @RequestBody InsertChatbotRequestDto insertChatbotRequestDto) {
        return ResultResponse.success(chatbotService.insertChatbot(insertChatbotRequestDto));
    }

    @GetMapping("/get-dates/chatbot")
    public ResponseEntity<?> getChatbotDates(@Valid @RequestParam int childUserId,
                                             @Valid @RequestParam int year,
                                             @Valid @RequestParam int month) {
        return ResponseEntity.ok(chatbotService.getChatbotDates(childUserId, year, month));
    }

    @GetMapping("/get-chatbot")
    public ResponseEntity<?> getChatbot(@Valid @RequestParam int childUserId,
                                        @Valid @RequestParam LocalDate date) {
        return ResponseEntity.ok(chatbotService.getChatbot(childUserId, date));
    }
}
