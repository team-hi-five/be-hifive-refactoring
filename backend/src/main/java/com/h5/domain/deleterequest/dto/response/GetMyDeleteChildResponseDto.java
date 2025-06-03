package com.h5.domain.deleterequest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "내 삭제 요청에 포함된 자녀 정보 응답 DTO")
public class GetMyDeleteChildResponseDto {

    @Schema(description = "자녀 사용자 ID", example = "123")
    private final Integer childUserId;

    @Schema(description = "자녀 이름", example = "홍길동")
    private final String childName;

    @Schema(description = "자녀 프로필 이미지 URL",
            example = "https://example.com/profiles/child123.jpg")
    private final String childUserProfileUrl;

    @Schema(description = "자녀 성별", example = "M")
    private final String gender;

    @Schema(description = "자녀 나이", example = "8")
    private final Integer age;

    @Schema(description = "부모 이름", example = "김철수")
    private final String parentUserName;

    @Schema(description = "자녀 생년월일", example = "2017-05-20")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birth;

    @Schema(description = "부모 전화번호", example = "010-1234-5678")
    private final String parentUserPhone;

    @Schema(description = "부모 이메일", example = "parent@example.com")
    private final String parentUserEmail;

    @Schema(description = "첫 상담 일자", example = "2025-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate firConsultDt;

    @Schema(description = "자녀 관심사", example = "축구")
    private final String interest;

    @Schema(description = "추가 정보", example = "특별 지원 필요 없음")
    private final String additionalInfo;
}
