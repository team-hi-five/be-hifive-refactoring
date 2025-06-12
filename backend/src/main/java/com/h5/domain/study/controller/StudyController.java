package com.h5.domain.study.controller;

import com.h5.domain.study.dto.request.SaveStudyLogRequest;
import com.h5.domain.study.dto.request.StartStudyChapterRequest;
import com.h5.domain.study.dto.request.StartStudyStageRequest;
import com.h5.domain.study.dto.response.EndStudyChapterResponse;
import com.h5.domain.study.dto.response.SaveStudyLogResponse;
import com.h5.domain.study.dto.response.StartStudyChapterResponse;
import com.h5.domain.study.dto.response.StartStudyStageResponse;
import com.h5.domain.study.service.StudyService;
import com.h5.global.dto.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Study API", description = "아동 학습 챕터 및 스테이지 관리 API")
@RestController
@RequestMapping("/study")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StudyController {

    private final StudyService studyService;

    @Operation(
            summary = "학습 챕터 시작",
            description = "아동 학습 챕터를 시작하고, 생성된 챕터 ID를 응답합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @PostMapping("/chapters")
    public ResultResponse<StartStudyChapterResponse> startStudyChapter(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "시작 요청 DTO (childUserId, studyChapterId 포함)")
            @RequestBody StartStudyChapterRequest startStudyChapterRequest
    ) {
        return ResultResponse.success(
                studyService.startStudyChapter(startStudyChapterRequest)
        );
    }

    @Operation(
            summary = "학습 챕터 종료",
            description = "학습 챕터 종료 처리 후, 종료된 챕터 ID를 응답합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @PutMapping("/{childStudyChapterId}/end")
    public ResultResponse<EndStudyChapterResponse> endStudyChapter(
            @Parameter(description = "종료할 챕터 ID", required = true, example = "1")
            @PathVariable Integer childStudyChapterId
    ) {
        return ResultResponse.success(
                studyService.endStudyChapter(childStudyChapterId)
        );
    }

    @Operation(
            summary = "학습 스테이지 시작",
            description = "아동 학습 스테이지를 시작하고, 생성된 스테이지 ID를 응답합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @PostMapping("/stage")
    public ResultResponse<StartStudyStageResponse> startStudyStage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "시작 요청 DTO (childStudyChapterId, gameStageId 포함)")
            @RequestBody StartStudyStageRequest startStudyStageRequest
    ) {
        return ResultResponse.success(
                studyService.startStudyStage(startStudyStageRequest)
        );
    }

    @Operation(
            summary = "학습 로그 저장",
            description = "비디오 로그와 텍스트 로그를 저장하고, 각 로그 ID를 응답합니다."
    )
    @PreAuthorize("hasAuthority('ROLE_PARENT')")
    @PostMapping("/save-log")
    public ResultResponse<SaveStudyLogResponse> saveStudyLog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "저장 요청 DTO (childGameStageId, 감정값, STT, 유사도 등 포함)")
            @RequestBody SaveStudyLogRequest saveStudyLogRequest
    ) {
        return ResultResponse.success(
                studyService.saveStudyLog(saveStudyLogRequest)
        );
    }

}
