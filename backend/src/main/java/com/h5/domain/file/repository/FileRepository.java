package com.h5.domain.file.repository;

import com.h5.domain.file.entity.FileEntity;
import com.h5.domain.file.entity.TblType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {
    List<FileEntity> findAllByTblTypeAndTblId(@NotNull TblType tblType, @NotNull Integer tblId);
}
