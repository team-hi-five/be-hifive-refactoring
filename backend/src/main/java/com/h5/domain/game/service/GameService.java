package com.h5.domain.game.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.asset.entity.GameChapterEntity;
import com.h5.domain.asset.entity.GameStageEntity;
import com.h5.domain.asset.repository.GameChapterRepository;
import com.h5.domain.asset.repository.GameStageRepository;
import com.h5.domain.auth.service.AuthenticationService;
import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.child.repository.ChildUserRepository;
import com.h5.domain.emotion.entity.EmotionEntity;
import com.h5.domain.emotion.repository.EmotionRepository;
import com.h5.domain.game.dto.request.*;
import com.h5.domain.game.dto.response.EndGameChapterResponse;
import com.h5.domain.game.dto.response.SaveGameLogResponse;
import com.h5.domain.game.dto.response.StartGameChapterResponse;
import com.h5.domain.game.dto.response.StartGameStageResponse;
import com.h5.domain.game.entity.AiLogEntity;
import com.h5.domain.game.entity.ChildGameChapterEntity;
import com.h5.domain.game.entity.ChildGameStageEntity;
import com.h5.domain.game.entity.GameLogEntity;
import com.h5.domain.game.repository.AiLogRepository;
import com.h5.domain.game.repository.ChildGameChapterRepository;
import com.h5.domain.game.repository.ChildGameStageRepository;
import com.h5.domain.game.repository.GameLogRepository;
import com.h5.domain.statistic.entity.StatisticEntity;
import com.h5.domain.statistic.repository.StatisticRepository;
import com.h5.domain.user.child.service.ChildUserService;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final AiLogRepository aiLogRepository;
    private final ChildGameChapterRepository childGameChapterRepository;
    private final ChildGameStageRepository childGameStageRepository;
    private final GameLogRepository gameLogRepository;
    private final GameChapterRepository gameChapterRepository;
    private final GameStageRepository gameStageRepository;
    private final StatisticRepository statisticRepository;
    private final EmotionRepository emotionRepository;
    private final AuthenticationService authenticationService;
    private final ChildUserService childUserService;

    /**
     * 새로운 게임 챕터를 시작하고, 해당 기록의 ID를 반환합니다.
     *
     * @param req 시작할 게임 챕터 정보(자녀 ID, 게임 챕터 ID)
     * @return 생성된 ChildGameChapterEntity의 ID를 담은 {@link StartGameChapterResponse}
     * @throws BusinessException USER_NOT_FOUND 또는 GAME_NOT_FOUND
     */
    public StartGameChapterResponse startGameChapter(StartGameChapterRequest req) {
        ChildUserEntity childUser = childUserService.findByIdOrThrow(req.getChildUserId());

        GameChapterEntity gameChapter = gameChapterRepository.findById(req.getGameChapterId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));

        ChildGameChapterEntity toSave = ChildGameChapterEntity.builder()
                .childUserEntity(childUser)
                .gameChapterEntity(gameChapter)
                .startAt(LocalDateTime.now())
                .build();

        ChildGameChapterEntity saved = childGameChapterRepository.save(toSave);
        return StartGameChapterResponse.builder()
                .childGameChapterId(saved.getId())
                .build();
    }

    /**
     * 게임 챕터를 종료 처리하고, 해당 챕터 통계를 업데이트합니다.
     *
     * @param childGameChapterId 종료할 ChildGameChapterEntity의 ID
     * @return 종료된 챕터의 ID를 담은 {@link EndGameChapterResponse}
     * @throws BusinessException GAME_NOT_FOUND 또는 접근 권한이 없는 경우 GAME_ACCESS_DENY
     */
    public EndGameChapterResponse endGameChapter(Integer childGameChapterId) {
        String email = authenticationService.getCurrentUserEmail();

        ChildGameChapterEntity childGameChapterEntity = childGameChapterRepository.findByIdAndChildUserEntity_ParentUserEntity_Email(childGameChapterId, email)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_ACCESS_DENY));

        childGameChapterEntity.setEndAt(LocalDateTime.now());
        childGameChapterRepository.save(childGameChapterEntity);

        updateAnalytics(childGameChapterEntity);

        return EndGameChapterResponse.builder()
                .childGameChapterId(childGameChapterEntity.getId())
                .build();
    }

    /**
     * 새로운 게임 스테이지를 시작하고, 해당 기록의 ID를 반환합니다.
     *
     * @param req 시작할 게임 스테이지 정보(ChildGameChapterId, GameStageId)
     * @return 생성된 ChildGameStageEntity의 ID를 담은 {@link StartGameStageResponse}
     * @throws BusinessException GAME_NOT_FOUND
     */
    public StartGameStageResponse startGameStage(StartGameStageRequest req) {
        GameStageEntity gameStage = gameStageRepository.findById(req.getGameStageId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));

        ChildGameChapterEntity chapter = childGameChapterRepository.findById(req.getChildGameChapterId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));

        ChildGameStageEntity toSave = ChildGameStageEntity.builder()
                .gameStageEntity(gameStage)
                .childGameChapterEntity(chapter)
                .build();

        ChildGameStageEntity saved = childGameStageRepository.save(toSave);

        return StartGameStageResponse.builder()
                .childGameStageId(saved.getId())
                .build();
    }

    /**
     * 게임 플레이 결과(로그)와 AI 분석 결과를 저장합니다.
     *
     * @param req 저장할 게임 로그 및 AI 로그 정보
     * @return 생성된 GameLogEntity 및 AiLogEntity의 ID를 담은 {@link SaveGameLogResponse}
     * @throws BusinessException USER_NOT_FOUND 또는 GAME_NOT_FOUND
     */
    public SaveGameLogResponse saveGameLog(SaveGameLogRequest req) {
        ChildGameStageEntity stage = childGameStageRepository.findById(req.getChildGameStageId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));
        ChildUserEntity child = childUserService.findByIdOrThrow(req.getChildUserId());
        GameStageEntity gameStage = gameStageRepository.findById(req.getGameStageId())
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));

        GameLogEntity gameLog = GameLogEntity.builder()
                .selectedOpt(req.getSelectedOption())
                .corrected(req.getCorrected())
                .submitAt(LocalDateTime.now())
                .consulted(req.getConsulted())
                .childGameStageEntity(stage)
                .childUserEntity(child)
                .gameStageEntity(gameStage)
                .build();
        gameLog = gameLogRepository.save(gameLog);

        AiLogEntity aiLog = AiLogEntity.builder()
                .gameLogEntity(gameLog)
                .fHappy(req.getFHappy())
                .fAnger(req.getFAnger())
                .fSad(req.getFSad())
                .fPanic(req.getFPanic())
                .fFear(req.getFFear())
                .tHappy(req.getTHappy())
                .tAnger(req.getTAnger())
                .tSad(req.getTSad())
                .tPanic(req.getTPanic())
                .tFear(req.getTFear())
                .stt(req.getStt())
                .aiAnalyze(req.getAiAnalysis())
                .build();
        aiLog = aiLogRepository.save(aiLog);

        return SaveGameLogResponse.builder()
                .gameLogId(gameLog.getId())
                .aiLogId(aiLog.getId())
                .build();
    }

    /**
     * 챕터 종료 시 관련 스테이지별 통계 업데이트를 수행합니다.
     *
     * @param chapter 업데이트할 대상 ChildGameChapterEntity
     * @throws BusinessException GAME_NOT_FOUND
     */
    private void updateAnalytics(ChildGameChapterEntity chapter) {
        List<ChildGameStageEntity> stages = childGameStageRepository
                .findAllByChildGameChapterEntity_Id(chapter.getId());

        if (stages.isEmpty()) {
            throw new BusinessException(DomainErrorCode.GAME_NOT_FOUND);
        }

        int childUserId    = chapter.getChildUserEntity().getId();
        LocalDateTime startAt     = chapter.getStartAt();
        LocalDateTime endAt       = chapter.getEndAt();
        int gameChapterId  = chapter.getGameChapterEntity().getId();

        Map<Integer, List<ChildGameStageEntity>> stagesByStageId =
                stages.stream()
                        .collect(Collectors.groupingBy(s -> s.getGameStageEntity().getId()));

        stagesByStageId.keySet()
                .forEach(stageId ->
                        updateStatisticForGameStage(
                                childUserId,
                                stageId,
                                startAt,
                                endAt,
                                gameChapterId
                        )
                );
    }

    /**
     * 특정 스테이지에 대한 플레이 로그를 기반으로 통계를 계산 및 저장합니다.
     *
     * @param childUserId   자녀 사용자 ID
     * @param gameStageId   게임 스테이지 ID
     * @param startAt       챕터 시작 일시
     * @param endAt         챕터 종료 일시
     * @param gameChapterId 게임 챕터 ID (통계의 챕터 구분용)
     * @throws BusinessException GAME_NOT_FOUND 또는 USER_NOT_FOUND
     */
    private void updateStatisticForGameStage(
            Integer childUserId,
            Integer gameStageId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer gameChapterId
    ) {
        // 1) 게임 스테이지 → 감정 ID 추출
        GameStageEntity gameStage = findGameStage(gameStageId);
        int emotionId = gameStage.getEmotionEntity().getId();

        // 2) 기간 내 로그 조회·정렬
        List<GameLogEntity> logs = fetchSortedGameLogs(childUserId, gameStageId, startAt, endAt);

        // 3) 정답 여부 및 시도 횟수 계산
        boolean isCorrect = logs.stream().anyMatch(GameLogEntity::getCorrected);
        int whenCorrect = findFirstCorrectAttempt(logs);

        // 4) 통계 엔티티 로드 또는 생성
        StatisticEntity stat = getOrCreateStatistic(emotionId, childUserId);

        // 5) 전체 통계 업데이트
        applyOverallStats(stat, logs.size(), isCorrect, whenCorrect);

        // 6) 챕터별 통계 업데이트
        applyStageStats(stat, gameChapterId, logs.size(), isCorrect);

        // 7) 저장
        statisticRepository.save(stat);
    }

    /**
     * ID로 게임 스테이지 엔티티를 조회합니다.
     *
     * @param gameStageId 게임 스테이지 ID
     * @return 조회된 {@link GameStageEntity}
     * @throws BusinessException GAME_NOT_FOUND
     */
    private GameStageEntity findGameStage(int gameStageId) {
        return gameStageRepository.findById(gameStageId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));
    }

    /**
     * 주어진 기간 내 해당 스테이지의 모든 로그를 조회하여 제출일시 기준 오름차순으로 정렬합니다.
     *
     * @param userId  자녀 사용자 ID
     * @param stageId 게임 스테이지 ID
     * @param start   시작 일시
     * @param end     종료 일시
     * @return 정렬된 {@link GameLogEntity} 리스트
     */
    private List<GameLogEntity> fetchSortedGameLogs(
            Integer userId,
            Integer stageId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        List<GameLogEntity> logs = gameLogRepository
                .findAllByChildUserEntity_IdAndGameStageEntity_IdAndSubmitAtBetween(
                        userId, stageId, start, end
                )
                .orElse(List.of());
        logs.sort(Comparator.comparing(GameLogEntity::getSubmitAt));
        return logs;
    }

    /**
     * 로그 리스트에서 첫 번째 정답까지 걸린 시도 횟수를 계산합니다.
     *
     * @param logs 플레이 로그 리스트
     * @return 정답이 나온 시도 횟수(정답이 없으면 전체 로그 수 + 1)
     */
    private int findFirstCorrectAttempt(List<GameLogEntity> logs) {
        return IntStream.range(0, logs.size())
                .filter(i -> logs.get(i).getCorrected())
                .findFirst()
                .orElse(logs.size()) + 1;
    }

    /**
     * 감정 및 사용자 기준으로 통계 엔티티를 조회하거나, 없으면 초기값으로 생성합니다.
     *
     * @param emotionId   감정 엔티티 ID
     * @param childUserId 자녀 사용자 ID
     * @return 조회 혹은 생성된 {@link StatisticEntity}
     * @throws BusinessException GAME_NOT_FOUND 또는 USER_NOT_FOUND
     */
    private StatisticEntity getOrCreateStatistic(Integer emotionId, Integer childUserId) {
        return statisticRepository
                .findByEmotionEntity_IdAndChildUserEntity_Id(emotionId, childUserId)
                .orElseGet(() -> {
                    EmotionEntity emo = emotionRepository.findById(emotionId)
                            .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_NOT_FOUND));
                    ChildUserEntity child = childUserService.findByIdOrThrow(childUserId);
                    StatisticEntity initial = StatisticEntity.builder()
                            .emotionEntity(emo)
                            .childUserEntity(child)
                            .rating(0)
                            .trialCnt(0)
                            .crtCnt(0)
                            .stageTryCnt1(0).stageTryCnt2(0).stageTryCnt3(0).stageTryCnt4(0).stageTryCnt5(0)
                            .stageCrtCnt1(0).stageCrtCnt2(0).stageCrtCnt3(0).stageCrtCnt4(0).stageCrtCnt5(0)
                            .stageCrtRate1(BigDecimal.ZERO).stageCrtRate2(BigDecimal.ZERO)
                            .stageCrtRate3(BigDecimal.ZERO).stageCrtRate4(BigDecimal.ZERO)
                            .stageCrtRate5(BigDecimal.ZERO)
                            .build();
                    return statisticRepository.save(initial);
                });
    }

    /**
     * 전체 플레이 통계(trialCnt, crtCnt, rating)를 업데이트합니다.
     *
     * @param stat       업데이트할 통계 엔티티
     * @param trialCnt   해당 스테이지 시도 횟수
     * @param isCorrect  해당 스테이지에서 정답 여부
     * @param whenCorrect 정답까지 걸린 시도 횟수
     */
    private void applyOverallStats(
            StatisticEntity stat,
            Integer trialCnt,
            Boolean isCorrect,
            Integer whenCorrect
    ) {
        stat.setTrialCnt(stat.getTrialCnt() + trialCnt);
        if (isCorrect) {
            stat.setCrtCnt(stat.getCrtCnt() + 1);
        }
        final int BASIC_SCORE = 100;
        double delta = (whenCorrect < 3) ? (1.0 / whenCorrect) * BASIC_SCORE : 0.0;
        stat.setRating(stat.getRating() + (int) delta);
    }

    /**
     * 챕터별 통계(stageTryCnt, stageCrtCnt, stageCrtRate)를 업데이트합니다.
     *
     * @param stat       업데이트할 통계 엔티티
     * @param chapterId  게임 챕터 ID
     * @param trialCnt   해당 챕터 시도 횟수
     * @param isCorrect  해당 챕터에서 정답 여부
     */
    private void applyStageStats(
            StatisticEntity stat,
            int chapterId,
            int trialCnt,
            boolean isCorrect
    ) {
        switch (chapterId) {
            case 1 -> {
                stat.setStageTryCnt1(stat.getStageTryCnt1() + trialCnt);
                stat.setStageCrtCnt1(stat.getStageCrtCnt1() + (isCorrect ? 1 : 0));
                stat.setStageCrtRate1(calculateRate(stat.getStageCrtCnt1(), stat.getStageTryCnt1()));
            }
            case 2 -> {
                stat.setStageTryCnt2(stat.getStageTryCnt2() + trialCnt);
                stat.setStageCrtCnt2(stat.getStageCrtCnt2() + (isCorrect ? 1 : 0));
                stat.setStageCrtRate2(calculateRate(stat.getStageCrtCnt2(), stat.getStageTryCnt2()));
            }
            case 3 -> {
                stat.setStageTryCnt3(stat.getStageTryCnt3() + trialCnt);
                stat.setStageCrtCnt3(stat.getStageCrtCnt3() + (isCorrect ? 1 : 0));
                stat.setStageCrtRate3(calculateRate(stat.getStageCrtCnt3(), stat.getStageTryCnt3()));
            }
            case 4 -> {
                stat.setStageTryCnt4(stat.getStageTryCnt4() + trialCnt);
                stat.setStageCrtCnt4(stat.getStageCrtCnt4() + (isCorrect ? 1 : 0));
                stat.setStageCrtRate4(calculateRate(stat.getStageCrtCnt4(), stat.getStageTryCnt4()));
            }
            case 5 -> {
                stat.setStageTryCnt5(stat.getStageTryCnt5() + trialCnt);
                stat.setStageCrtCnt5(stat.getStageCrtCnt5() + (isCorrect ? 1 : 0));
                stat.setStageCrtRate5(calculateRate(stat.getStageCrtCnt5(), stat.getStageTryCnt5()));
            }
            default -> {}
        }
    }

    /**
     * 제공된 정답 수와 시도 수로 성공률(rate)을 계산합니다.
     *
     * @param correctCnt 정답 카운트
     * @param tryCnt     시도 카운트
     * @return 성공률을 나타내는 {@link BigDecimal}
     */
    private BigDecimal calculateRate(int correctCnt, int tryCnt) {
        if (tryCnt == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf((double) correctCnt / tryCnt);
    }

}
