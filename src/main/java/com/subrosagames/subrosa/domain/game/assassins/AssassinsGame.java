package com.subrosagames.subrosa.domain.game.assassins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Function;
import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.RuleType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
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
     * @param lifecycle game lifecycle
     */
    public AssassinsGame(GameEntity gameEntity, Lifecycle lifecycle) {
        super(gameEntity, lifecycle);
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
//        switch (assignmentType) {
//            case ROUND_ROBIN:
                performRoundRobinAssignment(players); //break;
//            case MUTUAL_INTEREST:
//                performMutualInterestAssignment(players); break;
//            case MELEE:
//                performMeleeAssignment(players); break;
//        }
    }

    private void performMeleeAssignment(List<Player> players) {
    }



    private void performMutualInterestAssignment(List<Player> players) {
        shuffleAndAssign(players, new Function<Player[], Void>() {
            @Override
            public Void apply(@Nullable Player[] players) {
                assert players != null && players.length == 2 : "Invalid player arguments supplied to assignment function";
                LOG.debug("Player {} and player {} are assigned each other as targets", players[0].getId(), players[1].getId());
                players[0].addTarget(players[1]);
                players[1].addTarget(players[0]);
                return null;
            }
        });
    }

    private void performRoundRobinAssignment(List<Player> players) {
        shuffleAndAssign(players, new Function<Player[], Void>() {
            @Override
            public Void apply(@Nullable Player[] players) {
                assert players != null && players.length == 2 : "Invalid player arguments supplied to assignment function";
                LOG.debug("Player {} is assigned player {} as a target", players[0].getId(), players[1].getId());
                players[0].addTarget(players[1]);
                return null;
            }
        });
    }

    private void shuffleAndAssign(List<Player> playerList, Function<Player[], Void> assignFunction) {
        ArrayList<Player> players = new ArrayList<Player>(playerList);
        Collections.shuffle(players);
        Player previous = null;
        for (Player player : players) {
            if (previous == null) {
                Player last = players.get(players.size() - 1);
                assignFunction.apply(new Player[] { player, last });
            } else {
                assignFunction.apply(new Player[] { player, previous });
            }
            previous = player;
        }
    }

    private List<? extends Rule> getAssassinGameRules() {
        return null;
    }

}
