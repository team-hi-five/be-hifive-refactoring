package com.h5.domain.chatbot.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.chatbot.document.ChatBotDocument;
import com.h5.domain.chatbot.dto.request.InsertChatbotRequest;
import com.h5.domain.chatbot.dto.response.ChatbotMessageResponse;
import com.h5.domain.chatbot.dto.response.GetChatbotDatesResponse;
import com.h5.domain.chatbot.dto.response.GetChatbotResponse;
import com.h5.domain.chatbot.repository.ChatbotRepository;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 챗봇 대화 저장 및 조회와 관련된 비즈니스 로직을 담당하는 서비스 클래스입니다.
 * <p>
 * - insertChatbot: 사용자의 챗봇 대화 요청 DTO를 받아 Document로 변환하여 저장합니다.
 * - getChatbotDates: 특정 자녀(childUserId)와 월(year, month)에 해당하는 날짜 목록을 조회합니다.
 * - getChatbot: 특정 자녀(childUserId)와 날짜(date)에 해당하는 챗봇 대화 내역을 조회합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotRepository chatbotRepository;

    /**
     * 사용자가 입력한 챗봇 대화 목록을 저장합니다.
     * <p>
     * 1. 오늘 날짜에 이미 챗봇 대화가 저장된 적이 있으면 예외를 던집니다.
     * 2. InsertChatbotRequestDto로부터 ChatBotDocument 리스트를 생성하여
     *    현재 시각을 chatBotUseDttm으로 설정한 뒤, 일괄 저장(saveAll)합니다.
     * </p>
     *
     * @param insertChatbotRequest 챗봇 대화 저장 요청 정보를 담은 DTO
     * @throws BusinessException 이미 오늘 저장된 챗봇 대화가 존재하는 경우 발생
     */
    @Transactional
    public void issueChatbot(InsertChatbotRequest insertChatbotRequest) {
        int childUserId = insertChatbotRequest.getChatbotMessageList().get(0).getChildUserId();

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        boolean alreadySaved = chatbotRepository.existsByChildUserIdAndChatBotUsedAtBetween(
                childUserId, startOfDay, endOfDay
        );
        if (alreadySaved) {
            throw new BusinessException(DomainErrorCode.CHATBOT_ALREADY_SAVED);
        }

        List<ChatBotDocument> documents = insertChatbotRequest.getChatbotMessageList().stream()
                .map(msg -> ChatBotDocument.builder()
                        .childUserId(childUserId)
                        .chatBotUsedAt(now)
                        .sender(msg.getSender())
                        .messageIndex(msg.getMessageIndex())
                        .message(msg.getMessage())
                        .build())
                .collect(Collectors.toList());

        chatbotRepository.saveAll(documents);
    }


    /**
     * 특정 자녀 ID와 연도(year), 월(month)에 해당하는 챗봇 대화 가능 날짜 목록을 조회합니다.
     * <p>
     * 1. YearMonth를 사용해 조회 범위의 시작(LocalDateTime at start of month)과
     *    끝(LocalDateTime at end of month)을 계산합니다.
     * 2. 해당 범위 내에 저장된 ChatBotDocument를 조회하여, 존재하지 않으면 예외를 던집니다.
     * 3. 조회된 Document들로부터 LocalDate만 추출하여 중복을 제거하고 정렬한 후 반환합니다.
     * </p>
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @param year        조회할 연도 (예: 2025)
     * @param month       조회할 월 (1~12)
     * @return GetChatbotDatesResponseDto 조회된 날짜 목록을 담은 DTO
     * @throws BusinessException 해당 기간에 저장된 챗봇 대화가 없을 경우 발생
     */
    @Transactional(readOnly = true)
    public GetChatbotDatesResponse getChatbotDates(int childUserId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime startDate = ym.atDay(1).atStartOfDay();
        LocalDateTime endDate = ym.atEndOfMonth().atTime(23, 59, 59);

        List<ChatBotDocument> docs = chatbotRepository
                .findByChildUserIdAndChatBotUsedAtBetween(childUserId, startDate, endDate)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.CHATBOT_NOT_FOUND));

        List<LocalDate> dates = docs.stream()
                .map(doc -> doc.getChatBotUsedAt().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return GetChatbotDatesResponse.builder()
                .dateList(dates)
                .build();
    }

    /**
     * 특정 자녀 ID와 날짜(date)에 해당하는 챗봇 대화 내역을 조회합니다.
     * <p>
     * 1. LocalDate를 사용해 조회 범위의 시작(LocalDate at start of day)과
     *    끝(LocalDateTime at end of day)을 계산합니다.
     * 2. 해당 범위 내에 저장된 ChatBotDocument를 모두 조회하여, 존재하지 않으면 예외를 던집니다.
     * 3. 조회된 Document 리스트를 응답 DTO로 반환합니다.
     * </p>
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @param date        조회할 날짜 (ISO 형식, yyyy-MM-dd)
     * @return GetChatbotResponseDto 조회된 챗봇 대화 내역을 담은 DTO
     * @throws BusinessException 해당 날짜에 저장된 챗봇 대화가 없을 경우 발생
     */
    @Transactional(readOnly = true)
    public GetChatbotResponse getChatbot(int childUserId, LocalDate date) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);

        List<ChatbotMessageResponse> chatbotMessageList = chatbotRepository
                .findProjectedByChildUserIdAndChatBotUsedAtBetween(
                        childUserId, startDate, endDate
                )
                .orElseThrow(() -> new BusinessException(DomainErrorCode.CHATBOT_NOT_FOUND));

        return GetChatbotResponse.builder()
                .chatBotMessageList(chatbotMessageList)
                .build();
    }
}
