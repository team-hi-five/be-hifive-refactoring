package com.h5.domain.board.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "NoticeUpdateRequest", description = "공지사항 수정 요청 DTO")
public class NoticeUpdateRequest {

    @NotNull(message = "공지사항 ID를 입력해야 합니다.")
    @Schema(description = "수정할 공지사항의 ID", example = "123")
    private Integer id;

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    @Schema(description = "공지사항 제목", example = "Updated 공지사항 제목")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Schema(description = "공지사항 내용", example = "Updated 공지사항 내용")
    private String content;
}
