package com.h5.domain.game.repository;

import com.h5.domain.game.entity.ChildGameChapterEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChildGameChapterRepository extends JpaRepository<ChildGameChapterEntity, Integer> {
    List<ChildGameChapterEntity> findByChildUserEntity_IdAndStartAtBetween(int childUserId, LocalDateTime startAt, LocalDateTime endAt);

    @EntityGraph(attributePaths = {"childUserEntity.parentUserEntity"})
    Optional<ChildGameChapterEntity> findByIdAndChildUserEntity_ParentUserEntity_Email(Integer childGameChapterId, String parentEmail);
}
