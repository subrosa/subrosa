package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.game.EnrollmentField;
import com.subrosagames.subrosa.domain.game.EnrollmentFieldType;

/**
 * Persisted game enrollment field.
 */
@Entity
@Table(name = "enrollment_field")
public class EnrollmentFieldEntity implements EnrollmentField {

    @EmbeddedId
    private EnrollmentFieldPk primaryKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    @MapsId("gameId")
    private GameEntity game;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private EnrollmentFieldType type;

    @JsonIgnore
    @Column
    private Integer index;

    @PrePersist
    @PreUpdate
    private void prepareIndex() {
        if (game != null) {
            index = game.getPlayerInfo().indexOf(this);
        }
    }

    @Override
    public String getFieldId() {
        return primaryKey.getFieldId();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setType(EnrollmentFieldType type) {
        this.type = type;
    }

    @Override
    public EnrollmentFieldType getType() {
        return type;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }

    public void setPrimaryKey(EnrollmentFieldPk primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
