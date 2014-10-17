package com.subrosagames.subrosa.domain.player.persistence;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 * Represents the compound primary key for player images.
 */
@Embeddable
class PlayerImagePk implements Serializable {

    private static final long serialVersionUID = 7804431808559707967L;

    private Integer playerId;

    private Integer imageId;

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
