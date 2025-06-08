package com.h5.domain.board.faq.dto.request;

import com.h5.domain.board.faq.entity.FaqEntity;
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
@Schema(name = "FaqIssueRequestDto", description = "FAQ 작성 요청 DTO")
public class FaqIssueRequest {

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 가능합니다.")
    @Schema(description = "FAQ 제목", example = "어린이 방과후 학습 프로그램 안내")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Schema(description = "FAQ 내용", example = "방과후 프로그램은 오후 4시부터 6시까지 운영됩니다.")
    private String content;

    @NotNull(message = "유형을 선택해야 합니다.")
    @Schema(description = "FAQ 유형 (예: USAGE, CHILD, ETC)", example = "CHILD")
    private FaqEntity.Type type;
}
