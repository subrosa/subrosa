package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.image.Image;
import org.apache.commons.lang.NotImplementedException;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents a generic game.
 */
public class AssassinsGame extends Game {

    @Override
    public void startGame() {
        throw new NotImplementedException("Attempted to get events for a non-specific game");
    }

    @Override
    public void makeAssignments() {
        throw new NotImplementedException("Attempted to get events for a non-specific game");
    }

    @Override
    public void endGame() {
        throw new NotImplementedException("Attempted to get events for a non-specific game");
    }

    @Override
    public List<? extends GameEvent> getEvents() {
        throw new NotImplementedException("Attempted to get events for a non-specific game");
    }

    @Override
    public List<? extends GameRule> getRules() {
        throw new NotImplementedException("Attempted to get rules for a non-specific game");
    }

    @Override
    public List<? extends Participant> getPlayers() {
        throw new NotImplementedException("Attempted to get players for a non-specific game");
    }
}
