package com.h5.domain.asset.entity;

import com.h5.domain.game.entity.ChildGameChapterEntity;
import com.h5.domain.study.entity.ChildStudyChapterEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "game_chapter")
public class GameChapterEntity {

    @Id
    @Column(name = "game_chapter_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Size(max = 100)
    @NotNull
    @Column(name = "chapter_pic", nullable = false, length = 100)
    private String chapterPic;

    @OneToMany(mappedBy = "gameChapterEntity")
    private Set<ChildGameChapterEntity> childGameChapterEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "gameChapterEntity")
    private Set<ChildStudyChapterEntity> childStudyChapterEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "gameChapterEntity")
    private Set<GameStageEntity> gameStageEntities = new LinkedHashSet<>();

}
