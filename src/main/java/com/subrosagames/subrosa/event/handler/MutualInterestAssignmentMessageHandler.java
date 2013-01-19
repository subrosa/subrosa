package com.subrosagames.subrosa.event.handler;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.assassins.AssignmentType;
import com.subrosagames.subrosa.domain.game.assassins.TargetAssigner;

/**
 *
 */
@Component
public class MutualInterestAssignmentMessageHandler extends AbstractMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MutualInterestAssignmentMessageHandler.class);

    @Override
    public void process(Game game, Map<String, Serializable> properties) throws Exception {
        LOG.debug("Performing mutual interest assignment of players for game {}", game.getId());
        TargetAssigner.assignTargets(game.getPlayers(), AssignmentType.MUTUAL_INTEREST);
    }
}
