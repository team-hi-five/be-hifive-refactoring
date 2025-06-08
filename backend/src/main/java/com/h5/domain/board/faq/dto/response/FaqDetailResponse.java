package com.h5.domain.board.faq.dto.response;

import com.h5.domain.board.faq.entity.FaqEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(name = "FaqDetailResponseDto", description = "FAQ 상세 조회 응답 DTO")
public class FaqDetailResponse {

    @Schema(description = "FAQ 식별자", example = "123")
    private final Integer id;

    @Schema(description = "FAQ 제목", example = "어린이 방과후 학습 프로그램 안내")
    private final String title;

    @Schema(description = "FAQ 내용", example = "방과후 프로그램은 오후 4시부터 6시까지 운영됩니다.")
    private final String content;

    @Schema(description = "작성자 이메일 또는 이름", example = "parent@example.com")
    private final String writer;

    @Schema(description = "FAQ 유형", example = "USAGE")
    private final FaqEntity.Type type;
}
