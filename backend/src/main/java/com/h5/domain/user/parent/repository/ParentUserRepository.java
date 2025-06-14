package com.h5.domain.user.parent.repository;

import com.h5.domain.user.parent.entity.ParentUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentUserRepository extends JpaRepository<ParentUserEntity, Integer> {
    Optional<ParentUserEntity> findByEmail(String email);

    Optional<ParentUserEntity> findEmailByNameAndPhone(String name, String phone);

    Optional<ParentUserEntity> findByEmailAndDeletedAtIsNull(String email);
}
