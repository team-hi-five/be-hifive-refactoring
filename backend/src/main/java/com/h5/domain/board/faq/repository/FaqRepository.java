package com.h5.domain.board.faq.repository;

import com.h5.domain.board.faq.entity.FaqEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Integer> {

    Optional<FaqEntity> findByIdAndDeletedAtIsNull(Integer id);

    Page<FaqEntity> findByConsultantUser_Center_IdAndTitleContainingAndDeletedAtIsNull(Integer centerId, String title, Pageable pageable);

    Page<FaqEntity> findByConsultantUser_Center_IdAndConsultantUser_NameContainingAndDeletedAtIsNull(Integer centerId, String writer, Pageable pageable);

    Page<FaqEntity> findAllByConsultantUser_Center_IdAndDeletedAtIsNull(Integer centerId, Pageable pageable);
}

