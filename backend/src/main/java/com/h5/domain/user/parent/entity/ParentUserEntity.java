package com.h5.domain.user.parent.entity;

import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_dttm")
    private LocalDateTime createDttm;

    @Column(name = "delete_dttm")
    private LocalDateTime deleteDttm;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "update_dttm", insertable = false, updatable = false)
    private LocalDateTime updateDttm;

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
    public ParentUserEntity(Integer id, String name, String email, String pwd, String phone, LocalDateTime createDttm, LocalDateTime deleteDttm, LocalDateTime updateDttm, Integer consultantUserId, boolean tempPwd) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.phone = phone;
        this.createDttm = createDttm;
        this.deleteDttm = deleteDttm;
        this.updateDttm = updateDttm;
        this.consultantUserId = consultantUserId;
        this.tempPwd = tempPwd;
    }
}
