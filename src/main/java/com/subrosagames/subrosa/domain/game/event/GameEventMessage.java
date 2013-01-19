package com.subrosagames.subrosa.domain.game.event;

import java.io.Serializable;
import java.util.Map;

/**
 * Base class for game event messages.
 */
public class GameEventMessage implements Serializable {

    private static final long serialVersionUID = -623557190936708194L;

    private int gameId;
    private Map<String, Serializable> properties;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Map<String, Serializable> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }
}
