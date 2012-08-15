package com.subrosa.game;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.game.Participant;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public interface Game {

    int getId();

    String getName();

    String getDescription();

    GameType getGameType();

    BigDecimal getPrice();

    Date getStartTime();

    Date getEndTime();

    String getTimezone();

    Image getImage();

    List<? extends com.subrosa.event.GameEvent> getEvents();

    List<? extends GameRule> getRules();

    List<? extends Participant> getPlayers();
}
