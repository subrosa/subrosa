package org.eludo.subrosa.game;

import org.eludo.subrosa.event.GameEvent;

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

    GameImage getImage();

    List<? extends GameEvent> getEvents();

    List<? extends GameRule> getRules();

    List<? extends Participant> getPlayers();
}
