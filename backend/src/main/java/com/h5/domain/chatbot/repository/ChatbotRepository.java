package com.h5.domain.chatbot.repository;

import com.h5.domain.chatbot.document.ChatBotDocument;
import com.h5.domain.chatbot.dto.response.ChatbotMessageResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotRepository extends MongoRepository<ChatBotDocument, String> {

    Optional<List<ChatBotDocument>> findByChildUserIdAndChatBotUsedAtBetween(
            Integer childUserId, LocalDateTime chatBotUseDttmStart, LocalDateTime chatBotUseDttmEnd
    );

    Optional<List<ChatbotMessageResponse>> findProjectedByChildUserIdAndChatBotUsedAtBetween(
            int childUserId, LocalDateTime start, LocalDateTime end
    );

    boolean existsByChildUserIdAndChatBotUsedAtBetween(Integer childUserId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
