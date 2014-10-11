package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.subrosagames.subrosa.domain.game.RequiredAttribute;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;

/**
 * Persisted required game player attribute.
 */
@Entity
@Table(name = "required_attribute")
public class RequiredAttributeEntity extends BaseEntity implements RequiredAttribute {

    @Id
    @SequenceGenerator(name = "requiredAttributeSeq", sequenceName = "required_attribute_required_attribute_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requiredAttributeSeq")
    @Column(name = "required_attribute_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;

    @Column
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
