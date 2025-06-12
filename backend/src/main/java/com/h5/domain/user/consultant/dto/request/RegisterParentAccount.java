package com.h5.domain.user.consultant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "부모 계정 등록 요청 DTO")
public class RegisterParentAccount {

    @Schema(description = "부모 이름", example = "김철수")
    private String parentName;

    @Schema(description = "부모 이메일", example = "parent@example.com")
    private String parentEmail;

    @Schema(description = "부모 전화번호", example = "010-1234-5678")
    private String parentPhone;

    @Schema(description = "아동 이름", example = "김영희")
    private String childName;

    @Schema(description = "아동 생년월일", example = "2015-08-21")
    private String childBirth;

    @Schema(description = "아동 성별", example = "F")
    private String childGender;

    @Schema(description = "첫 상담 일자", example = "2025-06-03")
    private String firstConsultDt;

    @Schema(description = "아동 관심사", example = "그림")
    private String childInterest;

    @Schema(description = "아동 추가 정보", example = "특별 지도가 필요함")
    private String childAdditionalInfo;
}
