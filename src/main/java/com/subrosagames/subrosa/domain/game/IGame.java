package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.image.Image;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public interface IGame {

    int getId();

    String getName();

    String getDescription();

    GameType getGameType();

    BigDecimal getPrice();

    Date getStartTime();

    Date getEndTime();

    String getTimezone();

    Image getImage();

    List<? extends GameEvent> getEvents();

    List<? extends GameRule> getRules();

    List<? extends Participant> getPlayers();
}
