package com.subrosagames.subrosa.domain.player.persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Represents the compound primary key for player attributes.
 *
 * @see PlayerAttribute
 */
@Embeddable
public class PlayerAttributePk implements Serializable {

    private static final long serialVersionUID = -4775496375422803428L;

    @Column(name = "player_id")
    private Integer playerId;

    @Column
    private String name;

    /**
     * Default constructor.
     */
    public PlayerAttributePk() {
    }

    /**
     * Construct for player and attribute name.
     *
     * @param playerId player id
     * @param name     attribute name
     */
    public PlayerAttributePk(Integer playerId, String name) {
        this.playerId = playerId;
        this.name = name;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
