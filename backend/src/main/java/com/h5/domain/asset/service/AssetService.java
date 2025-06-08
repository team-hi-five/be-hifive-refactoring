package com.h5.domain.asset.service;

import com.github.hyeonjaez.springcommon.exception.BusinessException;
import com.h5.domain.asset.dto.response.CardAssetResponseDto;
import com.h5.domain.asset.dto.response.ChapterAssetResponseDto;
import com.h5.domain.asset.dto.response.GetStageResponseDto;
import com.h5.domain.asset.dto.response.LoadAssetResponseDto;
import com.h5.domain.asset.dto.response.LoadCardResponseDto;
import com.h5.domain.asset.dto.response.LoadChapterAssetResponseDto;
import com.h5.domain.asset.entity.CardAssetEntity;
import com.h5.domain.asset.entity.GameAssetEntity;
import com.h5.domain.asset.entity.GameStageEntity;
import com.h5.domain.asset.repository.CardAssetRepository;
import com.h5.domain.asset.repository.GameAssetRepository;
import com.h5.domain.asset.repository.GameChapterRepository;
import com.h5.domain.asset.repository.GameStageRepository;
import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.child.repository.ChildUserRepository;
import com.h5.global.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게임 및 카드 자산 조회와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * - 자녀의 clearChapter 정보를 기반으로 게임 스테이지와 챕터 정보를 계산하고,
 *   해당 스테이지의 동영상, 보기 옵션, 정답, 카드 앞/뒤 정보를 {@link LoadAssetResponseDto}로 매핑하여 반환합니다.
 * - 특정 챕터 내 스터디용 자산 목록 조회, 챕터 목록 조회, 카드 목록 조회 등의 기능을 제공합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final ChildUserRepository childUserRepository;
    private final GameAssetRepository gameAssetRepository;
    private final CardAssetRepository cardAssetRepository;
    private final GameChapterRepository gameChapterRepository;
    private final GameStageRepository gameStageRepository;

    /**
     * 자녀 ID를 받아, 해당 자녀가 현재 클리어한 스테이지의 게임 자산을 조회합니다.
     * <p>
     * 1. 자녀 정보에서 clearChapter 값을 가져온 뒤, 챕터 번호와 스테이지 번호를 계산합니다.
     * 2. 계산된 챕터와 스테이지 번호를 사용해 {@link GameAssetEntity}에서 게임 자산을,
     *    {@link GameStageEntity}에서 정답을 조회합니다.
     * 3. 조회된 정보를 {@link LoadAssetResponseDto}로 변환하여 반환합니다.
     * </p>
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @return 해당 스테이지의 동영상 URL, 보기 옵션, 이미지, 상황 설명, 정답 등을 포함한 {@link LoadAssetResponseDto}
     * @throws BusinessException 자녀를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     *                          또는 게임 자산을 찾지 못한 경우(DomainErrorCode.GAME_ASSET_NOT_FOUND)
     */
    public LoadAssetResponseDto loadAsset(Integer childUserId) {
        ChildUserEntity child = findChildOrThrow(childUserId);
        int clearChapter = child.getClearChapter();

        int chapter = computeChapter(clearChapter);
        int stage = computeStage(clearChapter);
        int gameStageId = computeGameStageId(chapter, stage);

        int stageAnswer = fetchStageAnswer(gameStageId);

        GameAssetEntity gameAsset = gameAssetRepository
                .findByGameStageEntity_GameChapterEntity_IdAndGameStageEntity_Stage(chapter, stage)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_ASSET_NOT_FOUND));

        return buildLoadAssetResponse(gameAsset, stageAnswer, chapter, stage, null);
    }

    /**
     * 챕터 번호와 스테이지 번호를 받아 해당하는 게임 자산 및 카드 자산을 조회합니다.
     * <p>
     * 1. 주어진 chapter, stage 값으로 gameStageId를 계산합니다.
     * 2. {@link GameAssetEntity}에서 게임 자산을, {@link GameStageEntity}에서 정답을 조회합니다.
     * 3. {@link CardAssetEntity}에서 카드 자산을 조회합니다.
     * 4. 조회된 정보를 {@link LoadAssetResponseDto}로 변환하여 반환합니다.
     * </p>
     *
     * @param chapter 조회할 챕터 번호
     * @param stage   조회할 스테이지 번호
     * @return 해당 챕터·스테이지의 동영상, 보기 옵션, 카드 앞/뒤, 정답 등을 포함한 {@link LoadAssetResponseDto}
     * @throws BusinessException 게임 자산 또는 카드 자산을 찾지 못한 경우(DomainErrorCode.GAME_ASSET_NOT_FOUND)
     */
    public LoadAssetResponseDto loadAssetByStage(Integer chapter, Integer stage) {
        int gameStageId = computeGameStageId(chapter, stage);

        int stageAnswer = fetchStageAnswer(gameStageId);

        GameAssetEntity gameAsset = gameAssetRepository
                .findById(gameStageId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_ASSET_NOT_FOUND));

        CardAssetEntity cardAsset = cardAssetRepository
                .findByGameStageEntity_Id(gameStageId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_ASSET_NOT_FOUND));

        return buildLoadAssetResponse(gameAsset, stageAnswer, chapter, stage, cardAsset);
    }

    /**
     * 자녀 ID를 받아, 해당 자녀가 클리어한 총 스테이지(ID) 값 이하의 모든 카드 자산 목록을 조회합니다.
     * <p>
     * 1. 자녀 정보에서 clearChapter 값을 가져옵니다.
     * 2. {@link CardAssetEntity} 테이블에서 gameStageId가 clearChapter 이하인 엔티티 목록을 조회합니다.
     * 3. 조회된 엔티티 리스트를 {@link CardAssetResponseDto} 리스트로 변환하여 반환합니다.
     * </p>
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @return 카드 자산 목록을 포함한 {@link LoadCardResponseDto}
     * @throws BusinessException 해당 자녀를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    public LoadCardResponseDto loadCards(Integer childUserId) {
        ChildUserEntity child = findChildOrThrow(childUserId);
        int clearChapter = child.getClearChapter();

        List<CardAssetEntity> entities = cardAssetRepository.findByGameStageEntity_IdLessThanEqual(clearChapter);
        List<CardAssetResponseDto> cardDtos = entities.stream()
                .map(entity -> new CardAssetResponseDto(
                        entity.getGameStageEntity().getId(),
                        entity.getCardFront(),
                        entity.getCardBack()
                ))
                .collect(Collectors.toList());

        return LoadCardResponseDto.builder()
                .cardAssetList(cardDtos)
                .build();
    }

    /**
     * 자녀 ID를 받아, 해당 자녀가 이용 가능한(등록된) 챕터 목록과 한도(limit) 정보를 조회합니다.
     * <p>
     * 1. 자녀 정보에서 clearChapter 값을 가져와 limit을 계산합니다 (limit = clearChapter / 5 + 1).
     * 2. 모든 {@link com.h5.domain.asset.entity.GameChapterEntity}를 조회하여
     *    {@link ChapterAssetResponseDto} 리스트로 변환합니다.
     * 3. {@link LoadChapterAssetResponseDto}에 챕터 리스트와 limit을 담아 반환합니다.
     * </p>
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @return 챕터 목록과 limit 정보를 포함한 {@link LoadChapterAssetResponseDto}
     * @throws BusinessException 해당 자녀를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    public LoadChapterAssetResponseDto loadChapterAsset(Integer childUserId) {
        ChildUserEntity child = findChildOrThrow(childUserId);
        int clearChapter = child.getClearChapter();
        int limit = clearChapter / 5 + 1;

        List<ChapterAssetResponseDto> chapterDtos = gameChapterRepository.findAll().stream()
                .map(chapterEntity -> ChapterAssetResponseDto.builder()
                        .gameChapterId(chapterEntity.getId())
                        .title(chapterEntity.getTitle())
                        .chapterPic(chapterEntity.getChapterPic())
                        .build())
                .collect(Collectors.toList());

        return LoadChapterAssetResponseDto.builder()
                .chapterAssetDtoList(chapterDtos)
                .limit(limit)
                .build();
    }

    /**
     * 특정 챕터 번호의 학습용(스터디) 자산 목록을 조회합니다.
     * <p>
     * 1. 요청받은 챕터 번호를 기반으로 해당 챕터에 속하는 스테이지 ID 범위(firstId ~ lastId)를 계산합니다.
     *    - firstId = (chapter - 1) * 5 + 1
     *    - lastId  = firstId + 4
     * 2. {@link GameAssetRepository}를 사용하여 범위 내 모든 {@link GameAssetEntity}를 조회합니다.
     * 3. 조회된 엔티티를 {@link LoadAssetResponseDto} 리스트로 변환하여 반환합니다.
     * </p>
     *
     * @param chapter 조회할 챕터 번호
     * @return 학습용 게임 자산 목록을 포함한 {@code List<LoadAssetResponseDto>}
     */
    public List<LoadAssetResponseDto> loadStudyAsset(Integer chapter) {
        int firstId = computeGameStageId(chapter, 1);
        int lastId = firstId + 4;

        List<GameAssetEntity> entities = gameAssetRepository.findByIdBetween(firstId, lastId);
        return entities.stream()
                .map(this::convertToDtoWithoutCard)
                .collect(Collectors.toList());
    }

    /**
     * 자녀 ID를 받아, 해당 자녀의 현재 클리어 상태의 챕터 번호와 스테이지 번호를 반환합니다.
     * <p>
     * 예) clearChapter가 7이면,
     *   - chapter = 7 / 5 + 1 = 2
     *   - stage   = 7 % 5 + 1 = 2
     * 반환값: chapter=2, stage=2
     * </p>
     *
     * @param childId 조회할 자녀 사용자 ID
     * @return 챕터 번호와 스테이지 번호를 포함한 {@link GetStageResponseDto}
     * @throws BusinessException 해당 자녀를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    public GetStageResponseDto getStage(int childId) {
        ChildUserEntity child = findChildOrThrow(childId);
        int clearChapter = child.getClearChapter();

        int chapter = computeChapter(clearChapter);
        int stage = computeStage(clearChapter);

        return GetStageResponseDto.builder()
                .chapter(chapter)
                .stage(stage)
                .build();
    }

    /**
     * 자녀 ID로 {@link ChildUserEntity}를 조회하여 반환합니다.
     *
     * @param childUserId 조회할 자녀 사용자 ID
     * @return 조회된 {@link ChildUserEntity}
     * @throws BusinessException 해당 자녀를 찾지 못한 경우(DomainErrorCode.USER_NOT_FOUND)
     */
    private ChildUserEntity findChildOrThrow(int childUserId) {
        return childUserRepository.findById(childUserId)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.USER_NOT_FOUND));
    }

    /**
     * clearChapter 값을 기반으로 챕터 번호를 계산합니다.
     * <p>
     * 공식: chapter = (clearChapter - 1) / 5 + 1
     * </p>
     *
     * @param clearChapter 자녀가 클리어한 총 스테이지 수
     * @return 계산된 챕터 번호
     */
    private int computeChapter(int clearChapter) {
        return ((clearChapter - 1) / 5) + 1;
    }

    /**
     * clearChapter 값을 기반으로 스테이지 번호를 계산합니다.
     * <p>
     * 공식: stage = (clearChapter - 1) % 5 + 1
     * </p>
     *
     * @param clearChapter 자녀가 클리어한 총 스테이지 수
     * @return 계산된 스테이지 번호
     */
    private int computeStage(int clearChapter) {
        return ((clearChapter - 1) % 5) + 1;
    }

    /**
     * 챕터 번호와 스테이지 번호를 기반으로 {@code gameStageId}를 계산합니다.
     * <p>
     * 공식: gameStageId = (chapter - 1) * 5 + stage
     * </p>
     *
     * @param chapter 챕터 번호
     * @param stage   스테이지 번호
     * @return 계산된 {@code gameStageId}
     */
    private int computeGameStageId(int chapter, int stage) {
        return (chapter - 1) * 5 + stage;
    }

    /**
     * gameStageId를 기반으로 {@link GameStageEntity#getCrtAns()}를 조회하여 반환합니다.
     *
     * @param gameStageId 조회할 게임 스테이지 ID
     * @return 해당 스테이지의 정답 키
     * @throws BusinessException 스테이지를 찾지 못한 경우(DomainErrorCode.GAME_ASSET_NOT_FOUND)
     */
    private int fetchStageAnswer(int gameStageId) {
        return gameStageRepository.findById(gameStageId)
                .map(GameStageEntity::getCrtAns)
                .orElseThrow(() -> new BusinessException(DomainErrorCode.GAME_ASSET_NOT_FOUND));
    }

    /**
     * {@link GameAssetEntity}와 정답, 챕터, 스테이지 정보를 합쳐 {@link LoadAssetResponseDto}를 생성합니다.
     * <p>
     * {@code cardAsset}가 {@code null}인 경우 카드 앞/뒤 정보는 DTO에 포함되지 않습니다.
     * </p>
     *
     * @param gameAsset   조회된 {@link GameAssetEntity}
     * @param answer      해당 스테이지의 정답 키
     * @param chapter     챕터 번호
     * @param stage       스테이지 번호
     * @param cardAsset   조회된 {@link CardAssetEntity} (없다면 {@code null})
     * @return 구성된 {@link LoadAssetResponseDto}
     */
    private LoadAssetResponseDto buildLoadAssetResponse(
            GameAssetEntity gameAsset,
            int answer,
            int chapter,
            int stage,
            CardAssetEntity cardAsset
    ) {
        LoadAssetResponseDto.LoadAssetResponseDtoBuilder builder = LoadAssetResponseDto.builder()
                .gameStageId(stage)
                .chapterId(chapter)
                .gameVideo(gameAsset.getGameSceneVideo())
                .options(new String[]{
                        gameAsset.getOpt1(),
                        gameAsset.getOpt2(),
                        gameAsset.getOpt3()
                })
                .optionImages(new String[]{
                        gameAsset.getOptPic1(),
                        gameAsset.getOptPic2(),
                        gameAsset.getOptPic3()
                })
                .situation(gameAsset.getSituation())
                .answer(answer);

        if (cardAsset != null) {
            builder.cardFront(cardAsset.getCardFront())
                    .cardBack(cardAsset.getCardBack());
        }

        return builder.build();
    }

    /**
     * {@link GameAssetEntity}만을 사용해 {@link LoadAssetResponseDto}를 생성합니다.
     * <p>
     * 카드 정보 없이 게임 자산만 반환해야 할 때 사용합니다.
     * </p>
     *
     * @param entity {@link GameAssetEntity}
     * @return 카드 정보 없이 구성된 {@link LoadAssetResponseDto}
     */
    private LoadAssetResponseDto convertToDtoWithoutCard(GameAssetEntity entity) {
        int gameStageId = entity.getId();
        int answer = fetchStageAnswer(gameStageId);
        int chapter = entity.getGameStageEntity().getGameChapterEntity().getId();
        int stage = computeStageFromEntity(entity);

        return LoadAssetResponseDto.builder()
                .gameStageId(stage)
                .chapterId(chapter)
                .gameVideo(entity.getGameSceneVideo())
                .options(new String[]{
                        entity.getOpt1(),
                        entity.getOpt2(),
                        entity.getOpt3()
                })
                .optionImages(new String[]{
                        entity.getOptPic1(),
                        entity.getOptPic2(),
                        entity.getOptPic3()
                })
                .situation(entity.getSituation())
                .answer(answer)
                .build();
    }

    /**
     * {@link GameAssetEntity}에서 챕터 정보를 추출해 스테이지 번호를 계산합니다.
     * <p>
     * 공식: stage = gameStageId - (chapter - 1) * 5
     * </p>
     *
     * @param entity {@link GameAssetEntity}
     * @return 계산된 스테이지 번호
     */
    private int computeStageFromEntity(GameAssetEntity entity) {
        int gameStageId = entity.getId();
        int chapter = entity.getGameStageEntity().getGameChapterEntity().getId();
        return gameStageId - (chapter - 1) * 5;
    }
}
