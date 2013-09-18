package com.subrosagames.subrosa.domain.gamesupport.assassin;

import java.util.List;

import com.subrosagames.subrosa.domain.game.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.player.Player;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Represents a generic game.
 */
@JsonPropertyOrder({ "ordnance" })
@JsonIgnoreProperties({ "password" })
@Entity
@DiscriminatorValue(AssassinGame.GAME_TYPE_ASSASSIN)
public class AssassinGame extends GameEntity {

    private static final Logger LOG = LoggerFactory.getLogger(AssassinGame.class);

    public static final String GAME_TYPE_ASSASSIN = "ASSASSIN";

    @Transient
    private String[] requiredEvents = {
    };

    @Transient
    private AssignmentType assignmentType;

    public AssassinGame() { }

    /**
     * Construct with given persistent entity and game lifecycle.
     * @param gameEntity game entity
     * @param lifecycle game lifecycle
     */
    public AssassinGame(GameEntity gameEntity, Lifecycle lifecycle) {
    }

    protected String[] getRequiredEvents() {
        return requiredEvents;
    }

    protected Class<? extends Enum> getPossibleAttributes() {
        return AssassinGameAttributeType.class;
    }

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
        List<Player> players = getPlayers();
        LOG.debug("Starting game {} with {} players", getId(), players.size());
    }

    private List<? extends Rule> getAssassinGameRules() {
        return null;
    }

}
