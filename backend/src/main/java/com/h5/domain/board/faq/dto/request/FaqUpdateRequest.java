package com.h5.domain.board.faq.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "FaqUpdateRequestDto", description = "FAQ 수정 요청 DTO")
public class FaqUpdateRequest {

    @NotNull(message = "FAQ ID를 반드시 입력해야 합니다.")
    @Schema(description = "수정할 FAQ의 ID", example = "123")
    private Integer id;

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    @Schema(description = "수정할 FAQ 제목", example = "Updated FAQ 제목")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Schema(description = "수정할 FAQ 내용", example = "Updated FAQ 내용")
    private String content;
}
