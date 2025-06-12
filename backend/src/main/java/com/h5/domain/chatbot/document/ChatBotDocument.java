package com.h5.domain.chatbot.document;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(collection = "chatbotDB")
@CompoundIndexes({
        @CompoundIndex(name = "idx_childId_date", def = "{'child_user_id': 1, 'chat_bot_used_at': 1}")
})
public class ChatBotDocument {

    @NotNull
    @Id
    @Field("chatbot_id")
    private String chatbotId;

    @NotNull
    @Field("child_user_id")
    private Integer childUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Field("chat_bot_used_at")
    private LocalDateTime chatBotUsedAt;

    @NotNull
    @Field("sender")
    private String sender;

    @NotNull
    @Field("message_index")
    private Integer messageIndex;

    @NotNull
    @Field("message")
    private String message;
}
