package com.subrosagames.subrosa.domain.game.assassins;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.RuleType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;

/**
 * Represents a generic game.
 */
public class AssassinsGame extends AbstractGame {

    private String[] requiredEvents = {
    };

    private String ordnance;

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

    public String getOrdnance() {
        return ordnance;
    }

    public void setOrdnance(String ordnance) {
        this.ordnance = ordnance;
    }

    public Map<RuleType, List<String>> getRules() {
        Map<RuleType, List<String>> rules = super.getRules();
        return rules;
    }

    private List<? extends Rule> getAssassinGameRules() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
