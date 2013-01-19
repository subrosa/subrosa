package com.subrosagames.subrosa.event.handler;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.event.GameEventMessage;

/**
 *
 */
public abstract class AbstractMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageHandler.class);

    @Autowired
    private GameFactory gameFactory;

    /**
     * Handle the given start game message.
     * @param message start game message
     * @throws Exception if message cannot be handled
     */
    public void process(GameEventMessage message) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game event of type {} for game {}", getClass(), message.getGameId());
        }
        Game game = gameFactory.getGameForId(message.getGameId());
        process(game, message.getProperties());
    }

    public abstract void process(Game game, Map<String, Serializable> properties) throws Exception;

}
