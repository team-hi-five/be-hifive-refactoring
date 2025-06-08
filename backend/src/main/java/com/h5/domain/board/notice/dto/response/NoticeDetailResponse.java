package com.h5.domain.board.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "NoticeDetailResponse", description = "공지사항 상세 조회 응답 DTO")
public class NoticeDetailResponse {

    @Schema(description = "공지사항 식별자", example = "123")
    private final Integer id;

    @Schema(description = "공지사항 제목", example = "새 학기 프로그램 안내")
    private final String title;

    @Schema(description = "공지사항 내용", example = "2025년 새 학기 방과후 수업이 3월 2일부터 시작됩니다.")
    private final String content;

    @Schema(description = "작성자 이름", example = "홍길동")
    private final String name;

    @Schema(description = "발행 일시", example = "2025-06-01T10:15:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime issuedAt;

    @Schema(description = "조회 수", example = "42")
    private final Integer viewCnt;
}
