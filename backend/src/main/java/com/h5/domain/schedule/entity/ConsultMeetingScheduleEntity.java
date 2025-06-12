package com.h5.domain.schedule.entity;

import com.h5.domain.user.child.entity.ChildUserEntity;
import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.user.parent.entity.ParentUserEntity;
import com.h5.global.enumerate.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "consult_meeting_schedule")
public class ConsultMeetingScheduleEntity implements BaseMeetingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_schedule_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "schedule_at", nullable = false)
    private LocalDateTime scheduleAt;

    @CreatedDate
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @NotNull
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", nullable = false)
    private ConsultantUserEntity host;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private ParentUserEntity parentUserEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_user_id", nullable = false)
    private ChildUserEntity childUserEntity;

    @Size(max = 225)
    @Column(name = "session_id", length = 225)
    private String sessionId;

}
