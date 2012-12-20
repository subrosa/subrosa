package com.subrosagames.subrosa.domain.game.assassins;

import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;

/**
 * Represents a generic game.
 */
public class AssassinsGame extends AbstractGame {

    private String[] requiredEvents = {
    };

    /**
     * Construct with given persistent entity and game lifecycle.
     * @param gameEntity game entity
     * @param lifecycle game lifecycle
     */
    public AssassinsGame(GameEntity gameEntity, Lifecycle lifecycle) {
        super(gameEntity, lifecycle);
    }

    @Override
    protected String[] getRequiredEvents() {
        return requiredEvents;
    }

}
