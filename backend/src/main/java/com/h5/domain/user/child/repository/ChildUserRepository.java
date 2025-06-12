package com.h5.domain.user.child.repository;

import com.h5.domain.user.child.entity.ChildUserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChildUserRepository extends JpaRepository<ChildUserEntity, Integer> {
    Optional<List<ChildUserEntity>> findByConsultantUserEntity_IdAndDeletedAtIsNull(Integer consultantUserEntityId);

    Optional<ChildUserEntity> findByIdAndConsultantUserEntity_IdAndDeletedAtIsNull(int childUserId, int consultantId);

    Optional<List<ChildUserEntity>> findByParentUserEntity_IdAndDeletedAtIsNull(Integer parentUserEntityId);

    Optional<ChildUserEntity> findNameByIdAndDeletedAtIsNull(Integer childUserId);

    Optional<List<ChildUserEntity>> findAllByParentUserEntity_IdAndDeletedAtIsNull(Integer parentUserId);

    Optional<List<ChildUserEntity>> findALlByNameContainingAndDeletedAtIsNull(@NotNull String name);

    @Modifying
    @Query("update ChildUserEntity c set c.deletedAt = :deletedAt where c.parentUserEntity.id = :parentUserId AND c.deletedAt IS NULL")
    void updateChildUserDeletedAtByParentUserEntity_Id(@Param("deletedAt") LocalDateTime deletedAt, @Param("parentUserId") Integer parentUserId);

}
