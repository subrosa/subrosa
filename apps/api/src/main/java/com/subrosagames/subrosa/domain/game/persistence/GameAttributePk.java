package com.subrosagames.subrosa.domain.game.persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Compound primary key for game attributes.
 */
@Embeddable
public class GameAttributePk implements Serializable {

    private static final long serialVersionUID = -4618965169814041329L;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "attribute_type")
    private String attributeType;

    /**
     * Default constructor.
     */
    public GameAttributePk() {
    }

    /**
     * Construct with game id and attribute type.
     *
     * @param gameId        game id
     * @param attributeType attribute type
     */
    public GameAttributePk(Integer gameId, String attributeType) {
        this.gameId = gameId;
        this.attributeType = attributeType;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
