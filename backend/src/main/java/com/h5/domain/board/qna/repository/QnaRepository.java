package com.h5.domain.board.qna.repository;

import com.h5.domain.board.qna.entity.QnaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QnaRepository extends JpaRepository<QnaEntity, Integer> {

    Page<QnaEntity> findAllByParentUser_IdAndDeletedAtIsNull(
            Integer parentUserId, Pageable pageable);

    Page<QnaEntity> findAllByParentUser_ConsultantUserEntity_IdAndDeletedAtIsNull(
            Integer consultantUserId, Pageable pageable);

    Page<QnaEntity> findAllByParentUser_IdAndTitleContainingAndDeletedAtIsNull(
            Integer parentUserId, String title, Pageable pageable);

    Page<QnaEntity> findAllByParentUser_IdAndParentUser_NameContainingAndDeletedAtIsNull(
            Integer parentUserId, String writer, Pageable pageable);

    Page<QnaEntity> findAllByParentUser_ConsultantUserEntity_IdAndTitleContainingAndDeletedAtIsNull(
            Integer filterId, String t, Pageable pageable
    );

    Page<QnaEntity> findAllByParentUser_ConsultantUserEntity_IdAndParentUser_NameContainingAndDeletedAtIsNull(
            Integer filterId, String w, Pageable pageable
    );

    Optional<QnaEntity> findByIdAndDeletedAtIsNull(int id);

}
