package com.subrosagames.subrosa.event.handler;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameEventMessage;

/**
 * Base class for message handlers.
 */
public abstract class AbstractMessageHandler implements MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageHandler.class);

    @Autowired
    private GameFactory gameFactory;

    /**
     * Handle the given start game message.
     *
     * @param message start game message
     * @throws MessageHandlingException if message cannot be handled
     */
    public void process(GameEventMessage message) throws MessageHandlingException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling game event of type {} for game {}", getClass(), message.getGameId());
        }
        Game game;
        try {
            game = gameFactory.getGame(message.getGameId());
        } catch (GameNotFoundException e) {
            throw new MessageHandlingException(e);
        }
        process(game, message.getProperties());
    }

    /**
     * Process message with properties for game.
     *
     * @param game       game
     * @param properties properties
     * @throws MessageHandlingException if something goes wrong
     */
    public abstract void process(Game game, Map<String, Serializable> properties) throws MessageHandlingException;

}
