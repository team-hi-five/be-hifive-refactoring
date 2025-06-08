package com.h5.domain.board.notice.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeIssueRequestDto {
    private String title;
    private String content;
}
