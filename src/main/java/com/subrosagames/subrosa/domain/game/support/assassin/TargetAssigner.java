package com.subrosagames.subrosa.domain.game.support.assassin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Function;
import com.subrosagames.subrosa.domain.player.Player;

/**
 *
 */
public final class TargetAssigner {

    private static final Logger LOG = LoggerFactory.getLogger(TargetAssigner.class);

    private TargetAssigner() { }

    public static void assignTargets(List<Player> players, AssignmentType assignmentType) {
        LOG.debug("Assigning targets for {} players using {} assignment type", players.size(), assignmentType);
        switch (assignmentType) {
            case ROUND_ROBIN:
                performRoundRobinAssignment(players); break;
            case MUTUAL_INTEREST:
                performMutualInterestAssignment(players); break;
            case MELEE:
                performMeleeAssignment(players); break;
        }
    }

    private static void performMeleeAssignment(List<Player> players) {
    }

    private static void performMutualInterestAssignment(List<Player> players) {
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

    private static void performRoundRobinAssignment(List<Player> players) {
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

    private static void shuffleAndAssign(List<Player> playerList, Function<Player[], Void> assignFunction) {
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

}
