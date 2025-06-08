package com.h5.domain.board.qna.repository;

import com.h5.domain.board.qna.entity.QnaCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaCommentRepository extends JpaRepository<QnaCommentEntity, Integer> {
    Optional<List<QnaCommentEntity>> findAllByQnaEntity_IdAndDeletedAtIsNull(Integer qnaEntityId);

    Optional<QnaCommentEntity> findByIdAndDeletedAtIsNull(Integer id);
}
