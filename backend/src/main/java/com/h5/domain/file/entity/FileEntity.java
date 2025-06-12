package com.h5.domain.file.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@FilterDef(name = "activeFilter")
@Filter(name = "activeFilter", condition = "delete_dttm is null")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Size(max = 255)
    @NotNull
    @Column(name = "origin_file_name", nullable = false)
    private String originFileName;

    @CreatedDate
    @NotNull
    @Column(name = "upload_at", nullable = false, updatable = false)
    private LocalDateTime uploadAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "tbl_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TblType tblType;

    @NotNull
    @Column(name = "tbl_id", nullable = false)
    private Integer tblId;

}
