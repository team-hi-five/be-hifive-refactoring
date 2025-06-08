package com.h5.domain.user.consultant.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "내 자녀 목록 조회 응답 DTO")
public class GetMyChildrenResponse {

    @Schema(description = "아동 사용자 ID", example = "101")
    private final Integer childUserId;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImgUrl;

    @Schema(description = "아동 이름", example = "홍길동")
    private final String childName;

    @Schema(description = "아동 생년월일", example = "2018-04-05")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birth;

    @Schema(description = "아동 나이", example = "7")
    private final Integer age;

    @Schema(description = "부모 이름", example = "김철수")
    private final String parentName;
}
