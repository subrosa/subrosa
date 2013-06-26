package com.subrosagames.subrosa.domain.game.event;

import java.util.Date;

/**
 * Model for game events.
 * Examples of game events include assignments and assignment type changes.
 */
public interface GameEvent {


    Integer getHistoryId();

    Integer getGameId();

    Integer getAssassinId();

    Integer getVictimId();

    String getObituary();

    String getType();

    Date getCreated();

    Date getModified();
}

