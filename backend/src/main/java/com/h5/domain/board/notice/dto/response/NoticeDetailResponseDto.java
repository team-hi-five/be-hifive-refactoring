package com.h5.domain.board.notice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDetailResponseDto {
    private Integer id;
    private String title;
    private String content;
    private String name;
    private LocalDateTime issuedAt;
    private Integer viewCnt;
}
