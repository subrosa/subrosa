package com.subrosagames.subrosa.domain.game.event;

import java.io.Serializable;

/**
 *
 */
public class AbstractMessage implements Serializable {

    private static final long serialVersionUID = -623557190936708194L;

    private int gameId;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
