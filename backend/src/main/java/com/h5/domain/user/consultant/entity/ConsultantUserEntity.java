package com.h5.domain.user.consultant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "consultant_user")
public class ConsultantUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultant_user_id", nullable = false)
    private Integer id;

    @Size(max = 10)
    @NotNull
    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Size(max = 30)
    @NotNull
    @Column(name = "email", nullable = false, length = 30)
    private String email;

    @Size(max = 75)
    @NotNull
    @Column(name = "pwd", nullable = false, length = 75)
    private String pwd;

    @Size(max = 13)
    @NotNull
    @Column(name = "phone", nullable = false, length = 13)
    private String phone;

    @CreatedDate
    @NotNull
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issueAt;

    @LastModifiedDate
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @Size(max = 45)
    @Column(name = "temp_pwd", length = 45)
    private boolean tempPwd;

}