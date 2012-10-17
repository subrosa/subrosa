package com.subrosagames.subrosa.domain.game;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;

/**
 * Represents a generic game.
 */
public class AssassinsGame extends Game {

    @Override
    public void startGame() {
    }

    @Override
    public void makeAssignments() {
    }

    @Override
    public void endGame() {
    }

    @Override
    public List<? extends GameEvent> getEvents() {
        return new ArrayList<GameEvent>();
    }

    @Override
    public List<? extends GameRule> getRules() {
        return new ArrayList<GameRule>();
    }

    @Override
    public List<? extends Participant> getPlayers() {
        return new ArrayList<Participant>();
    }
}
