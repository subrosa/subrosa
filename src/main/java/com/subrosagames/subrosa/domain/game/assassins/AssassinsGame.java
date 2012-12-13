package com.subrosagames.subrosa.domain.game.assassins;

import java.util.ArrayList;
import java.util.List;

import com.subrosagames.subrosa.domain.game.*;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameLifecycle;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a generic game.
 */
public class AssassinsGame extends AbstractGame {

    private static final Logger LOG = LoggerFactory.getLogger(AssassinsGame.class);

    private String[] requiredEvents = {
            "registrationStart",
            "registrationEnd",
            "gameStart",
            "gameEnd",
    };

    protected String[] getRequiredEvents() {
        return requiredEvents;
    }

    public AssassinsGame(GameEntity gameEntity, Lifecycle lifecycle) {
        super(gameEntity, lifecycle);
    }

    public AssassinsGame(int id) {
        super(id);
    }

    public AssassinsGame(GameEntity gameEntity) {
        super(gameEntity);
    }

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
    public List<? extends GameRule> getRules() {
        return new ArrayList<GameRule>();
    }

    @Override
    public List<? extends Participant> getPlayers() {
        return new ArrayList<Participant>();
    }

    public void startRegistration() {
        LOG.debug("Starting registration for game {}", getId());
    }

    public void endRegistration() {
        LOG.debug("Ending registration for game {}", getId());
    }
}
