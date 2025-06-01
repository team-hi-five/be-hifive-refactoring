package com.h5.domain.chatbot.repository;

import com.h5.domain.chatbot.document.ChatBotDocument;
import com.h5.domain.chatbot.dto.response.ChatbotMessageResponseDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotRepository extends MongoRepository<ChatBotDocument, String> {

    Optional<List<ChatBotDocument>> findByChildUserIdAndChatBotUseDttmBetween(
            Integer childUserId, LocalDateTime chatBotUseDttmStart, LocalDateTime chatBotUseDttmEnd
    );

    Optional<List<ChatbotMessageResponseDto>> findProjectedByChildUserIdAndChatBotUseDttmBetween(
            int childUserId, LocalDateTime start, LocalDateTime end
    );

    boolean existsByChildUserIdAndChatBotUseDttmBetween(Integer childUserId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
