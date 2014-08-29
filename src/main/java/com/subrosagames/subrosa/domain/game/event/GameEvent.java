package com.subrosagames.subrosa.domain.game.event;

import java.util.Date;

/**
 * Model for game events.
 * Examples of game events include assignments and assignment type changes.
 */
public interface GameEvent {

    public String getEvent();

    public Date getCreated();

    public Date getModified();

}

