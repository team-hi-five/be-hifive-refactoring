package com.h5.domain.statistic.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.statistic.mapper.StatisticMapper;
import com.h5.domain.emotion.entity.EmotionEntity;
import com.h5.domain.emotion.repository.EmotionRepository;
import com.h5.domain.game.entity.ChildGameChapterEntity;
import com.h5.domain.game.entity.GameLogEntity;
import com.h5.domain.game.repository.ChildGameChapterRepository;
import com.h5.domain.game.repository.GameLogRepository;
import com.h5.domain.statistic.dto.response.DataAnalysisResponse;
import com.h5.domain.statistic.dto.response.GetGameVideoDatesResponse;
import com.h5.domain.statistic.dto.response.GetGameVideoLengthResponse;
import com.h5.domain.statistic.entity.StatisticEntity;
import com.h5.domain.statistic.repository.StatisticRepository;
import com.h5.domain.user.child.service.ChildUserService;
import com.h5.global.exception.DomainErrorCode;
import com.h5.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticService {

    private final StatisticRepository statisticRepository;
    private final EmotionRepository emotionRepository;
    private final ChildGameChapterRepository childGameChapterRepository;
    private final GameLogRepository gameLogRepository;
    private final ChildUserService childUserService;
    private final StatisticMapper statisticMapper;

    /**
     * 아동 사용자의 감정별 통계 분석 결과를 조회한다.
     *
     * @param childUserId 아동 사용자 ID
     * @return 감정 ID를 키로 갖는 DataAnalysisResponseDto 맵
     * @throws BusinessException 해당 감정에 대한 통계 데이터가 없는 경우 예외 발생
     */
    public Map<Integer, DataAnalysisResponse> dataAnalysis(Integer childUserId) {
        String childUserName = childUserService.findByIdOrThrow(childUserId).getName();

        List<EmotionEntity> emotionEntityList = emotionRepository.findAll();

        List<StatisticEntity> stats = statisticRepository.findAllByChildUserEntity_Id(childUserId)
                .orElseGet(Collections::emptyList);

        Map<Integer, StatisticEntity> statsByEmotion = stats.stream()
                .collect(Collectors.toMap(
                        s -> s.getEmotionEntity().getId(),
                        Function.identity()
                ));

        return statisticMapper.toDataAnalysisResponse(
                emotionEntityList,
                statsByEmotion,
                childUserId,
                childUserName
        );
    }

    /**
     * 지정된 연월 내에 아동 사용자가 게임 챕터를 시작한 날짜 목록을 조회한다.
     *
     * @param childUserId 아동 사용자 ID
     * @param year 조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     * @return 고유한 날짜를 정렬하여 담은 DTO
     * @throws BusinessException 챕터 시작 기록이 없는 경우 예외 발생
     */
    public GetGameVideoDatesResponse getGameVideoDates(Integer childUserId, Integer year, Integer month) {
        LocalDateTime[] range = DateUtil.monthRange(year, month);
        LocalDateTime startDate = range[0];
        LocalDateTime endDate = range[1];

        List<ChildGameChapterEntity> chapters = childGameChapterRepository
                .findByChildUserEntity_IdAndStartAtBetween(childUserId, startDate, endDate);

        if (chapters.isEmpty()) {
            throw new BusinessException(DomainErrorCode.STATISTIC_NOT_FOUND);
        }

        return statisticMapper.toGameVideoDatesResponse(chapters);
    }

    /**
     * 지정된 연월 및 스테이지 내에서 아동 사용자의 게임 시도 인덱스와 게임 로그 ID 목록을 조회한다.
     *
     * @param childUserId 아동 사용자 ID
     * @param stageId 조회할 게임 스테이지 ID
     * @param year 조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     * @return 시도 인덱스와 게임 로그 ID를 담은 DTO 리스트
     * @throws BusinessException 게임 로그가 없는 경우 예외 발생
     */
    public List<GetGameVideoLengthResponse> getGameVideoLength(
            Integer childUserId,
            Integer stageId,
            Integer year,
            Integer month
    ) {
        LocalDateTime[] range = DateUtil.monthRange(year, month);
        LocalDateTime startDate = range[0];
        LocalDateTime endDate = range[1];

        List<GameLogEntity> logs = gameLogRepository
                .findAllByChildUserEntity_IdAndGameStageEntity_IdAndSubmitAtBetween(
                        childUserId, stageId, startDate, endDate
                )
                .orElseThrow(() -> new BusinessException(DomainErrorCode.STATISTIC_NOT_FOUND));

        return statisticMapper.toGameVideoLengthResponse(logs);
    }
}
