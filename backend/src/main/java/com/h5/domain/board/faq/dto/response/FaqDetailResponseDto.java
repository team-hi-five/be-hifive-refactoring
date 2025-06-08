package com.h5.domain.board.faq.dto.response;

import com.h5.domain.board.faq.entity.FaqEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FaqDetailResponseDto {
    private int id;
    private String title;
    private String content;
    private String writer;
    private FaqEntity.Type type;
}
