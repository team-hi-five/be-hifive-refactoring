package com.h5.domain.game.entity;

import com.h5.domain.asset.entity.GameChapterEntity;
import com.h5.domain.user.child.entity.ChildUserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "child_game_chapter")
public class ChildGameChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_game_chapter_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_user_id", nullable = false)
    private ChildUserEntity childUserEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_chapter_id", nullable = false)
    private GameChapterEntity gameChapterEntity;

    @NotNull
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Builder.Default
    @OneToMany(mappedBy = "childGameChapterEntity")
    private Set<ChildGameStageEntity> childGameStageEntities = new LinkedHashSet<>();

}