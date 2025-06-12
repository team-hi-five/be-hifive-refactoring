package com.h5.domain.study.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.asset.entity.GameChapterEntity;
import com.h5.domain.asset.entity.GameStageEntity;
import com.h5.domain.asset.repository.GameChapterRepository;
import com.h5.domain.asset.repository.GameStageRepository;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.study.dto.request.SaveStudyLogRequest;
import com.h5.domain.study.dto.request.StartStudyChapterRequest;
import com.h5.domain.study.dto.request.StartStudyStageRequest;
import com.h5.domain.study.dto.response.EndStudyChapterResponse;
import com.h5.domain.study.dto.response.SaveStudyLogResponse;
import com.h5.domain.study.dto.response.StartStudyChapterResponse;
import com.h5.domain.study.dto.response.StartStudyStageResponse;
import com.h5.domain.study.entity.ChildStudyChapterEntity;
import com.h5.domain.study.entity.ChildStudyStageEntity;
import com.h5.domain.study.entity.StudyTextLogEntity;
import com.h5.domain.study.entity.StudyVideoLogEntity;
import com.h5.domain.study.repository.ChildStudyChapterRepository;
import com.h5.domain.study.repository.ChildStudyStageRepository;
import com.h5.domain.study.repository.StudyTextLogRepository;
import com.h5.domain.study.repository.StudyVideoLogRepository;
import com.h5.domain.user.child.service.ChildUserService;
import com.h5.global.exception.DomainErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final ChildStudyChapterRepository childStudyChapterRepository;
    private final ChildStudyStageRepository childStudyStageRepository;
    private final GameChapterRepository gameChapterRepository;
    private final GameStageRepository gameStageRepository;
    private final StudyVideoLogRepository studyVideoLogRepository;
    private final StudyTextLogRepository studyTextLogRepository;
    private final ChildUserService childUserService;
    private final AuthenticationService authenticationService;

    /**
     * 학습 챕터를 시작하고, 시작 정보(ID)만 반환한다.
     *
     * @param req 시작 요청 DTO (childUserId, studyChapterId 포함)
     * @return 방금 생성된 ChildStudyChapter ID를 담은 응답 DTO
     * @throws BusinessException childUser 또는 gameChapter가 존재하지 않을 때
     */
    public StartStudyChapterResponse startStudyChapter(StartStudyChapterRequest req) {
        ChildUserEntity childUser = childUserService.findByIdOrThrow(req.getChildUserId());
        GameChapterEntity gameChapter = gameChapterRepository.findById(req.getStudyChapterId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));
        ChildStudyChapterEntity toSave = ChildStudyChapterEntity.builder()
                .childUserEntity(childUser)
                .gameChapterEntity(gameChapter)
                .startAt(LocalDateTime.now())
                .build();
        ChildStudyChapterEntity saved = childStudyChapterRepository.save(toSave);
        return StartStudyChapterResponse.builder()
                .childStudyChapterId(saved.getId())
                .build();
    }

    /**
     * 학습 챕터를 종료 처리하고, 종료된 챕터 ID를 반환한다.
     * 인증된 부모 사용자만 자신의 자녀 챕터를 종료할 수 있다.
     *
     * @param childStudyChapterId 종료할 ChildStudyChapter ID
     * @return 종료 처리된 ChildStudyChapter ID를 담은 응답 DTO
     * @throws BusinessException 존재하지 않거나 권한이 없는 경우
     */
    public EndStudyChapterResponse endStudyChapter(Integer childStudyChapterId) {
        String email = authenticationService.getCurrentUserEmail();

        ChildStudyChapterEntity chapter = childStudyChapterRepository.findByIdAndChildUserEntity_ParentUserEntity_Email(childStudyChapterId, email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_ACCESS_DENY));

        chapter.setEndAt(LocalDateTime.now());
        childStudyChapterRepository.save(chapter);
        return EndStudyChapterResponse.builder()
                .childStudyChapterId(chapter.getId())
                .build();
    }

    /**
     * 학습 스테이지를 시작하고, 생성된 스테이지 ID를 반환한다.
     *
     * @param req 시작 요청 DTO (childStudyChapterId, gameStageId 포함)
     * @return 방금 생성된 ChildStudyStage ID를 담은 응답 DTO
     * @throws BusinessException 해당 챕터 또는 스테이지가 없을 때
     */
    public StartStudyStageResponse startStudyStage(StartStudyStageRequest req) {
        GameStageEntity gameStage = gameStageRepository.findById(req.getGameStageId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));
        ChildStudyChapterEntity chapter = childStudyChapterRepository.findById(req.getChildStudyChapterId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));

        ChildStudyStageEntity toSave = ChildStudyStageEntity.builder()
                .gameStageEntity(gameStage)
                .childStudyChapterEntity(chapter)
                .build();
        ChildStudyStageEntity saved = childStudyStageRepository.save(toSave);

        return StartStudyStageResponse.builder()
                .childStudyStageId(saved.getId())
                .build();
    }

    /**
     * 비디오 로그와 텍스트 로그를 저장하고, 각 로그의 ID를 반환한다.
     *
     * @param req 저장 요청 DTO (childGameStageId, 감정값, STT, 유사도 등 포함)
     * @return 저장된 비디오/텍스트 로그 ID를 담은 응답 DTO
     * @throws BusinessException 해당 스테이지 엔티티가 없을 때
     */
    public SaveStudyLogResponse saveStudyLog(SaveStudyLogRequest req) {
        ChildStudyStageEntity stage = childStudyStageRepository.findById(req.getChildGameStageId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        StudyVideoLogEntity videoLog = StudyVideoLogEntity.builder()
                .childStudyStageEntity(stage)
                .fHappy(req.getFHappy())
                .fAnger(req.getFAnger())
                .fSad(req.getFSad())
                .fPanic(req.getFPanic())
                .fFear(req.getFFear())
                .startAt(now)
                .endAt(now)
                .build();
        videoLog = studyVideoLogRepository.save(videoLog);

        StudyTextLogEntity textLog = StudyTextLogEntity.builder()
                .childStudyStageEntity(stage)
                .tHappy(req.getTHappy())
                .tAnger(req.getTAnger())
                .tSad(req.getTSad())
                .tPanic(req.getTPanic())
                .tFear(req.getTFear())
                .stt(req.getStt())
                .textSimilarity(req.getTextSimilarity())
                .startAt(now)
                .endAt(now)
                .build();
        textLog = studyTextLogRepository.save(textLog);

        return SaveStudyLogResponse.builder()
                .studyVideoLogId(videoLog.getId())
                .studyTextLogId(textLog.getId())
                .build();
    }
}
