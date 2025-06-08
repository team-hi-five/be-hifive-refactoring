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
    Optional<List<ChildUserEntity>> findByConsultantUserEntity_IdAndDeleteDttmIsNull(Integer consultantUserEntityId);

    Optional<ChildUserEntity> findByIdAndConsultantUserEntity_IdAndDeleteDttmIsNull(int childUserId, int consultantId);

    Optional<List<ChildUserEntity>> findByParentUserEntity_IdAndDeleteDttmIsNull(Integer parentUserEntityId);

    Optional<ChildUserEntity> findNameByIdAndDeleteDttmIsNull(Integer childUserId);

    Optional<List<ChildUserEntity>> findAllByParentUserEntity_IdAndDeleteDttmIsNull(Integer parentUserId);

    Optional<List<ChildUserEntity>> findALlByNameContainingAndDeleteDttmIsNull(@NotNull String name);

    @Modifying
    @Query("update ChildUserEntity c set c.deleteDttm = :deleteDttm where c.parentUserEntity.id = :parentUserId AND c.deleteDttm IS NULL")
    void updateChildUserDeleteDttmByParentUserEntity_Id(@Param("deleteDttm") LocalDateTime deleteDttm, @Param("parentUserId") Integer parentUserId);

}
