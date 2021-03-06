package com.subrosagames.subrosa.domain.game.support.assassin;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.player.Player;

/**
 * Represents a generic game.
 */
@JsonPropertyOrder("ordnance")
@JsonIgnoreProperties("password")
@Entity
@DiscriminatorValue(AssassinGame.GAME_TYPE_ASSASSIN)
public class AssassinGame extends BaseGame {

    /**
     * Game type identifier.
     */
    public static final String GAME_TYPE_ASSASSIN = "ASSASSIN";

    private static final Logger LOG = LoggerFactory.getLogger(AssassinGame.class);

    @Transient
    private String[] requiredEvents = {
    };

    @Transient
    private AssignmentType assignmentType;

    /**
     * Default constructor.
     */
    public AssassinGame() {
    }

    /**
     * Construct with given persistent entity and game lifecycle.
     *
     * @param gameEntity game entity
     */
    public AssassinGame(GameEntity gameEntity) {
    }

    protected String[] getRequiredEvents() {
        return requiredEvents;
    }

    protected Class<? extends Enum> getPossibleAttributes() {
        return AssassinGameAttributeType.class;
    }

    /**
     * Get ordnance for this game.
     *
     * @return ordnance
     */
    public String getOrdnance() {
        if (hasAttribute(AssassinGameAttributeType.ORDNANCE_TYPE)) {
            return getAttributes().get(AssassinGameAttributeType.ORDNANCE_TYPE.name()).getValue();
        }
        return null;
    }

    private boolean hasAttribute(Enum<? extends GameAttributeType> attributeType) {
        return getAttributes() != null && getAttributes().containsKey(attributeType.name());
    }

    public AssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    @Transactional
    @Override
    public void startGame() {
        List<? extends Player> players = getPlayers();
        LOG.debug("Starting game {} with {} players", getId(), players.size());
    }

    private List<? extends Rule> getAssassinGameRules() {
        return null;
    }

}
