package com.h5.domain.user.parent.entity;

import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "parent_user")
public class ParentUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_user_id", nullable = false)
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
    @Column(name = "consultant_user_id")
    private Integer consultantUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_user_id", nullable = false, insertable = false, updatable = false)
    private ConsultantUserEntity consultantUserEntity;

    @Column(name = "temp_pwd")
    private boolean tempPwd;

    @OneToMany(mappedBy = "parentUserEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChildUserEntity> childUserEntities = new HashSet<>();

    @Builder
    public ParentUserEntity(Integer id, String name, String email, String pwd, String phone, LocalDateTime issueAt, LocalDateTime updateAt, LocalDateTime deletedAt, Integer consultantUserId, boolean tempPwd) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.issueAt = issueAt;
        this.updateAt = updateAt;
        this.deletedAt = deletedAt;
        this.consultantUserId = consultantUserId;
        this.tempPwd = tempPwd;
    }
}
