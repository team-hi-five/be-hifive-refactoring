package com.h5.domain.board.faq.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaqUpdateRequestDto {
    private Integer id;
    private String title;
    private String content;
}
