package com.h5.domain.deleterequest.entity;

import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.user.parent.entity.ParentUserEntity;
import com.h5.global.enumerate.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "delete_user_request")
public class DeleteUserRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delete_user_request_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "delete_requested_at")
    private LocalDateTime deleteRequestedAt;

    @Column(name = "delete_confirmed_at")
    private LocalDateTime deleteConfirmedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "parent_user_id")
    private Integer parentUserId;

    @Column(name = "consultant_user_id")
    private Integer consultantUserId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_user_id", nullable = false, insertable = false, updatable = false)
    private ParentUserEntity parentUser;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_user_id", nullable = false, insertable = false, updatable = false)
    private ConsultantUserEntity consultantUser;

    @Builder
    public DeleteUserRequestEntity(Integer id, LocalDateTime deleteRequestedAt, LocalDateTime deleteConfirmedAt, Status status, Integer parentUserId, Integer consultantUserId) {
        this.id = id;
        this.deleteRequestedAt = deleteRequestedAt;
        this.deleteConfirmedAt = deleteConfirmedAt;
        this.status = status;
        this.parentUserId = parentUserId;
        this.consultantUserId = consultantUserId;
    }
}
