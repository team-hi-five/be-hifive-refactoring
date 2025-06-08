package com.h5.domain.board.notice.repository;

import com.h5.domain.board.notice.entity.NoticeEntity;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Integer> {

    Page<NoticeEntity> findByConsultantUser_Center_IdAndTitleContainingAndDeletedAtIsNull(Integer centerId, String title, Pageable pageable);

    Page<NoticeEntity> findByConsultantUser_Center_IdAndConsultantUser_NameContainingAndDeletedAtIsNull(Integer centerId, String writer, Pageable pageable);

    Page<NoticeEntity> findAllByConsultantUser_Center_IdAndDeletedAtIsNull(Integer centerId, Pageable pageable);

    Optional<NoticeEntity> findByIdAndDeletedAtIsNull(Integer id);
}
