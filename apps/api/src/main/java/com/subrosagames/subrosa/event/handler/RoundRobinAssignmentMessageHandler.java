package com.subrosagames.subrosa.event.handler;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.support.assassin.AssignmentType;
import com.subrosagames.subrosa.domain.game.support.assassin.TargetAssigner;

/**
 * Performs round robin assignments.
 */
public class RoundRobinAssignmentMessageHandler extends AbstractMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobinAssignmentMessageHandler.class);

    @Override
    public void process(Game game, Map<String, Serializable> properties) {
        LOG.debug("Performing round robin assignments for game {}", game.getId());
        TargetAssigner.assignTargets(game.getPlayers(), AssignmentType.ROUND_ROBIN);
    }
}
