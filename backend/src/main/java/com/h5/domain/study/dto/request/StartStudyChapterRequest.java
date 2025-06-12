package com.h5.domain.study.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "학습 챕터 시작 요청 DTO")
public class StartStudyChapterRequest {

    @Schema(description = "아동 사용자 ID", example = "123")
    @NotNull(message = "childUserId는 필수 입력값입니다.")
    private Integer childUserId;

    @Schema(description = "시작할 학습 챕터 ID", example = "5")
    @NotNull(message = "studyChapterId는 필수 입력값입니다.")
    private Integer studyChapterId;
}
