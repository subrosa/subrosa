package com.subrosagames.subrosa.domain.game;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;

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
