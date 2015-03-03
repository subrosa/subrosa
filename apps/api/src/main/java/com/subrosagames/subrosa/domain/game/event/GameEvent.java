package com.subrosagames.subrosa.domain.game.event;

import java.util.Date;

import com.subrosagames.subrosa.domain.game.Game;

/**
 * Model for game events.
 * Examples of game events include assignments and assignment type changes.
 */
public interface GameEvent {

    /**
     * Get game event id.
     *
     * @return game event id
     */
    Integer getId();

    /**
     * Get game event name.
     *
     * @return game event name
     */
    String getEvent();

    /**
     * Get created date.
     *
     * @return created date
     */
    Date getCreated();

    /**
     * Get last modified date.
     *
     * @return last modified date
     */
    Date getModified();

    /**
     * Get the game in which event applies.
     *
     * @return game
     */
    Game getGame();

}

