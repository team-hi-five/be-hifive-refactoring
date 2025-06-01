package com.h5.domain.asset.repository;

import com.h5.domain.asset.entity.GameAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameAssetRepository extends JpaRepository<GameAssetEntity, Integer> {

    Optional<GameAssetEntity> findByGameStageEntity_GameChapterEntity_IdAndGameStageEntity_Stage(int chapter, int stage);

    List<GameAssetEntity> findByIdBetween(Integer idBefore, Integer IdAfter);
}
