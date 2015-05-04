package com.subrosagames.subrosa.event.handler;

import com.subrosagames.subrosa.domain.game.event.GameEventMessage;

/**
 * Interface for message handlers.
 */
public interface MessageHandler {

    /**
     * Process given game event message.
     *
     * @param message game event message
     * @throws MessageHandlingException if something goes wrong
     */
    void process(GameEventMessage message) throws MessageHandlingException;
}
