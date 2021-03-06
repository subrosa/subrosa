package com.subrosagames.subrosa.event.handler;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.support.assassin.AssignmentType;
import com.subrosagames.subrosa.domain.game.support.assassin.TargetAssigner;

/**
 * Performs mutual interest assignments.
 */
public class MutualInterestAssignmentMessageHandler extends AbstractMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MutualInterestAssignmentMessageHandler.class);

    @Override
    public void process(Game game, Map<String, Serializable> properties) {
        LOG.debug("Performing mutual interest assignment of players for game {}", game.getId());
        TargetAssigner.assignTargets(game.getPlayers(), AssignmentType.MUTUAL_INTEREST);
    }
}
