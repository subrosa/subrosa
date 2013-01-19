package com.subrosagames.subrosa.domain.game.assassins;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.RuleType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.domain.player.Player;

/**
 * Represents a generic game.
 */
@JsonPropertyOrder({ "ordnance" })
public class AssassinsGame extends AbstractGame {

    private static final Logger LOG = LoggerFactory.getLogger(AssassinsGame.class);

    private String[] requiredEvents = {
    };

    private AssignmentType assignmentType;

    /**
     * Construct with given persistent entity and game lifecycle.
     * @param gameEntity game entity
     * @param lifecycleEntity game lifecycle
     */
    public AssassinsGame(GameEntity gameEntity, LifecycleEntity lifecycleEntity) {
        super(gameEntity, lifecycleEntity);
    }

    @Override
    protected String[] getRequiredEvents() {
        return requiredEvents;
    }

    protected Class<? extends Enum> getPossibleAttributes() {
        return AssassinGameAttributeType.class;
    }

    public String getOrdnance() {
        if (hasAttribute(AssassinGameAttributeType.ORDNANCE_TYPE)) {
            return getGameEntity().getAttributes().get(AssassinGameAttributeType.ORDNANCE_TYPE.name()).getValue();
        }
        return null;
    }

    private boolean hasAttribute(Enum<? extends GameAttributeType> attributeType) {
        return getGameEntity() != null
                && getGameEntity().getAttributes() != null
                && getGameEntity().getAttributes().containsKey(attributeType.name());
    }

    public AssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public Map<RuleType, List<String>> getRules() {
        Map<RuleType, List<String>> rules = super.getRules();
        return rules;
    }

    @Transactional
    @Override
    public void startGame() {
        List<Player> players = getPlayers();
        LOG.debug("Starting game {} with {} players", getId(), players.size());
    }

    private List<? extends Rule> getAssassinGameRules() {
        return null;
    }

}
