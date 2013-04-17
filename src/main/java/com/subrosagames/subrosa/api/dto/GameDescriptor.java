package com.subrosagames.subrosa.api.dto;

import java.util.List;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;

/**
 * Encapsulates the necessary information to create a game.
 */
public class GameDescriptor {

    private GameEntity info;
    private List<GameEvent> events;

    public GameEntity getInfo() {
        return info;
    }

    public void setInfo(GameEntity info) {
        this.info = info;
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }
}
