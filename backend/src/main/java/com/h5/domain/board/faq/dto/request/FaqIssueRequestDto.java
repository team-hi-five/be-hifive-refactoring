package com.h5.domain.board.faq.dto.request;

import com.h5.domain.board.faq.entity.FaqEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaqIssueRequestDto {
    private String title;
    private String content;
    private FaqEntity.Type type;
}
