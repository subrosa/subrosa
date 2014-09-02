package com.subrosagames.subrosa.domain.game.event;

import com.subrosagames.subrosa.domain.game.Game;

import java.util.Date;

/**
 * Model for game events.
 * Examples of game events include assignments and assignment type changes.
 */
public interface GameEvent {

    public Integer getId();

    public String getEvent();

    public Date getCreated();

    public Date getModified();

    public void setGame(Game game);

    public Game getGame();

}

