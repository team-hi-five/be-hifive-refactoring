package com.h5.domain.chatbot.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(collection = "chatbotDB")
@CompoundIndexes({
        @CompoundIndex(name = "idx_childId_date", def = "{'childUserId': 1, 'chatBotUseDttm': 1}")
})
public class ChatBotDocument {

    @NotNull
    @Id
    private String chatbotId;

    @NotNull
    private Integer childUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime chatBotUseDttm;

    @NotNull
    private String sender;

    @NotNull
    private Integer messageIndex;

    @NotNull
    private String message;
}
