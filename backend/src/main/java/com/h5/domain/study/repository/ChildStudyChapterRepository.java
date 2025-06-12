package com.h5.domain.study.repository;

import com.h5.domain.study.entity.ChildStudyChapterEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildStudyChapterRepository extends JpaRepository<ChildStudyChapterEntity, Integer> {
    @EntityGraph(attributePaths = {"childUserEntity.parentUserEntity"})
    Optional<ChildStudyChapterEntity> findByIdAndChildUserEntity_ParentUserEntity_Email(
            Integer chapterId,
            String parentEmail
    );
}
