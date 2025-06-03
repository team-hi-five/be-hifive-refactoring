package com.h5.domain.consultant.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아동 정보 조회 응답 DTO")
public class GetChildResponse {

    @Schema(description = "아동 사용자 ID", example = "123")
    private final Integer childUserId;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
    private final String profileImgUrl;

    @Schema(description = "아동 이름", example = "홍길동")
    private final String childName;

    @Schema(description = "아동 나이", example = "7")
    private final Integer age;

    @Schema(description = "아동 생년월일", example = "2018-04-05")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birth;

    @Schema(description = "아동 성별", example = "M")
    private final String gender;

    @Schema(description = "첫 상담 일자", example = "2025-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate firstConsultDate;

    @Schema(description = "아동 관심사", example = "축구")
    private final String interest;

    @Schema(description = "추가 정보", example = "특별 지원 필요 없음")
    private final String additionalInfo;

    @Schema(description = "부모 이름", example = "김철수")
    private final String parentName;

    @Schema(description = "부모 전화번호", example = "010-1234-5678")
    private final String parentPhone;

    @Schema(description = "부모 이메일", example = "parent@example.com")
    private final String parentEmail;
}
