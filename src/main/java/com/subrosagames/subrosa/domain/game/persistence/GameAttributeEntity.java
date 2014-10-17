package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * Persisted game attribute.
 */
@Entity
@Table(name = "game_attribute")
public class GameAttributeEntity {

    @EmbeddedId
    private GameAttributePk primaryKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    @MapsId("gameId")
    private GameEntity game;

    @Column
    private String value;

    /**
     * Default constructor.
     */
    public GameAttributeEntity() {
    }

    /**
     * Construct with given game and attribute.
     *
     * @param gameEntity game
     * @param attributeType attribute type
     * @param value attribute value
     */
    public GameAttributeEntity(GameEntity gameEntity, String attributeType, String value) {
        this.primaryKey = new GameAttributePk(gameEntity.getId(), attributeType);
        this.game = gameEntity;
        this.value = value;
    }

    public GameAttributePk getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(GameAttributePk primaryKey) {
        this.primaryKey = primaryKey;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
