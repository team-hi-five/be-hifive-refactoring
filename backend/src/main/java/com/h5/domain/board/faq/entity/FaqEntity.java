package com.h5.domain.board.faq.entity;

import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "faq")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class FaqEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_user_id", nullable = false)
    private ConsultantUserEntity consultantUser;

    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 6)
    private Type type;

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
}
