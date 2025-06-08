package com.h5.domain.user.child.entity;

import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import com.h5.domain.game.entity.GameLogEntity;
import com.h5.domain.user.parent.entity.ParentUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "child_user")
public class ChildUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_user_id", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "interest", nullable = false)
    private String interest;

    @Column(name = "first_consult_dt", nullable = false)
    private LocalDate firstConsultDt;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Lob
    @Column(name = "gender")
    private String gender;

    @Lob
    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "clear_chapter")
    private Integer clearChapter;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "parent_user_id")
    private Integer parentUserId;

    @Column(name = "consultant_user_id")
    private Integer consultantUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private ParentUserEntity parentUserEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultant_user_id", nullable = false)
    private ConsultantUserEntity consultantUserEntity;

    @Column(name = "delete_dttm")
    private String deleteDttm;

    @Builder.Default
    @OneToMany(mappedBy = "childUserEntity")
    private Set<GameLogEntity> gameLogEntities = new LinkedHashSet<>();

    @Builder
    public ChildUserEntity(Integer id, String interest, LocalDate firstConsultDt, LocalDate birth, String gender, String additionalInfo, Integer clearChapter, String name, Integer parentUserId, Integer consultantUserId, String deleteDttm, Set<GameLogEntity> gameLogEntities) {
        this.id = id;
        this.interest = interest;
        this.firstConsultDt = firstConsultDt;
        this.birth = birth;
        this.gender = gender;
        this.additionalInfo = additionalInfo;
        this.clearChapter = clearChapter;
        this.name = name;
        this.parentUserId = parentUserId;
        this.consultantUserId = consultantUserId;
        this.deleteDttm = deleteDttm;
        this.gameLogEntities = gameLogEntities;
    }
}