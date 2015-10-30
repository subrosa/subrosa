package com.subrosagames.subrosa.domain.game.support.assassin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.subrosagames.subrosa.domain.player.Player;

/**
 * Handles assignment of targets.
 */
public final class TargetAssigner {

    private static final Logger LOG = LoggerFactory.getLogger(TargetAssigner.class);

    private TargetAssigner() {
    }

    /**
     * Assign players targets according to the given assignment type.
     *  @param players        players
     * @param assignmentType assignment type
     */
    public static void assignTargets(List<? extends Player> players, AssignmentType assignmentType) {
        LOG.debug("Assigning targets for {} players using {} assignment type", players.size(), assignmentType);
        switch (assignmentType) {
            case ROUND_ROBIN:
                performRoundRobinAssignment(players);
                break;
            case MUTUAL_INTEREST:
                performMutualInterestAssignment(players);
                break;
            case MELEE:
                performMeleeAssignment(players);
                break;
            default:
                // throw new InvalidAssignmentTypeException("Bad assignment type given");
        }
    }

    private static void performMeleeAssignment(List<? extends Player> players) {
    }

    private static void performMutualInterestAssignment(List<? extends Player> players) {
        shuffleAndAssign(players, toAssign -> {
            assert toAssign != null && toAssign.length == 2 : "Invalid player arguments supplied to assignment function";
            LOG.debug("Player {} and player {} are assigned each other as targets", toAssign[0].getId(), toAssign[1].getId());
            toAssign[0].addTarget(toAssign[1]);
            toAssign[1].addTarget(toAssign[0]);
            return null;
        });
    }

    private static void performRoundRobinAssignment(List<? extends Player> players) {
        shuffleAndAssign(players, toAssign -> {
            assert toAssign != null && toAssign.length == 2 : "Invalid player arguments supplied to assignment function";
            LOG.debug("Player {} is assigned player {} as a target", toAssign[0].getId(), toAssign[1].getId());
            toAssign[0].addTarget(toAssign[1]);
            return null;
        });
    }

    private static void shuffleAndAssign(List<? extends Player> playerList, Function<Player[], Void> assignFunction) {
        List<Player> players = new ArrayList<>(playerList);
        Collections.shuffle(players);
        Player previous = null;
        for (Player player : players) {
            if (previous == null) {
                Player last = players.get(players.size() - 1);
                assignFunction.apply(new Player[]{ player, last });
            } else {
                assignFunction.apply(new Player[]{ player, previous });
            }
            previous = player;
        }
    }

}
