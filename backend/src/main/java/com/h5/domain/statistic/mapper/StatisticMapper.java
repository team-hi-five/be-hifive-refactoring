package com.h5.domain.statistic.mapper;

import com.h5.domain.emotion.entity.EmotionEntity;
import com.h5.domain.game.entity.ChildGameChapterEntity;
import com.h5.domain.game.entity.GameLogEntity;
import com.h5.domain.statistic.dto.response.DataAnalysisResponse;
import com.h5.domain.statistic.dto.response.GetGameVideoDatesResponse;
import com.h5.domain.statistic.dto.response.GetGameVideoLengthResponse;
import com.h5.domain.statistic.entity.StatisticEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class StatisticMapper {

    /**
     * 감정 목록과 통계 맵을 바탕으로, 아동 사용자의 감정별 분석 응답 DTO 맵으로 변환한다.
     * <p>
     * 통계 데이터가 하나도 없으면 빈 Map을 반환한다.
     *
     * @param emotionEntityList 감정 엔티티 리스트
     * @param statsByEmotion    감정 ID를 키로 하는 통계 엔티티 맵
     * @param childUserId       아동 사용자 ID
     * @param childName         아동 사용자 이름
     * @return 감정 ID를 키로 갖는 DataAnalysisResponse 맵 (데이터 없으면 빈 Map)
     */
    public Map<Integer, DataAnalysisResponse> toDataAnalysisResponse(
            List<EmotionEntity> emotionEntityList,
            Map<Integer, StatisticEntity> statsByEmotion,
            Integer childUserId,
            String childName
    ) {
        if (statsByEmotion.isEmpty()) {
            return Collections.emptyMap();
        }

        return emotionEntityList.stream()
                .filter(emotion -> statsByEmotion.containsKey(emotion.getId()))
                .map(emotion -> {
                    StatisticEntity stat = statsByEmotion.get(emotion.getId());
                    return DataAnalysisResponse.builder()
                            .childUserId(childUserId)
                            .childName(childName)
                            .emotionId(emotion.getId())
                            .rating(stat.getRating())
                            .totalTryCnt(stat.getTrialCnt())
                            .totalCrtCnt(stat.getCrtCnt())
                            .stageCrtCnt(List.of(
                                    stat.getStageCrtCnt1(),
                                    stat.getStageCrtCnt2(),
                                    stat.getStageCrtCnt3(),
                                    stat.getStageCrtCnt4(),
                                    stat.getStageCrtCnt5()))
                            .stageCrtRate(List.of(
                                    stat.getStageCrtRate1(),
                                    stat.getStageCrtRate2(),
                                    stat.getStageCrtRate3(),
                                    stat.getStageCrtRate4(),
                                    stat.getStageCrtRate5()))
                            .stageTryCnt(List.of(
                                    stat.getStageTryCnt1(),
                                    stat.getStageTryCnt2(),
                                    stat.getStageTryCnt3(),
                                    stat.getStageTryCnt4(),
                                    stat.getStageTryCnt5()))
                            .build();
                })
                .collect(Collectors.toMap(
                        DataAnalysisResponse::getEmotionId,
                        Function.identity()
                ));
    }

    /**
     * 게임 챕터 시작 날짜 리스트를 DTO로 변환한다.
     *
     * @param chapters ChildGameChapterEntity 리스트
     * @return 시작 날짜 목록을 담은 GetGameVideoDatesResponse
     */
    public GetGameVideoDatesResponse toGameVideoDatesResponse(
            List<ChildGameChapterEntity> chapters
    ) {
        List<LocalDate> dates = chapters.stream()
                .map(c -> c.getStartAt().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return GetGameVideoDatesResponse.builder()
                .dateList(dates)
                .build();
    }

    /**
     * 게임 로그 리스트를 시도 인덱스 및 로그 ID를 가진 DTO 리스트로 변환한다.
     *
     * @param logs GameLogEntity 리스트
     * @return GetGameVideoLengthResponse 리스트
     */
    public List<GetGameVideoLengthResponse> toGameVideoLengthResponse(
            List<GameLogEntity> logs
    ) {
        return IntStream.range(0, logs.size())
                .mapToObj(i -> GetGameVideoLengthResponse.builder()
                        .tryIndex(i)
                        .gameLogId(logs.get(i).getId())
                        .build())
                .collect(Collectors.toList());
    }
}
