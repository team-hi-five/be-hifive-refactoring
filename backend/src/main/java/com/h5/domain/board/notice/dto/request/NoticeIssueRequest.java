package com.h5.domain.board.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "NoticeIssueRequest", description = "공지사항 작성 요청 DTO")
public class NoticeIssueRequest {

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    @Schema(description = "공지사항 제목", example = "새 학기 프로그램 안내")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Schema(description = "공지사항 내용", example = "2025년 새 학기 방과후 수업이 3월 2일부터 시작됩니다.")
    private String content;
}
