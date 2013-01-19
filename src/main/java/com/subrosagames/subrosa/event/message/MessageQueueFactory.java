package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.GameEventMessage;
import com.subrosagames.subrosa.event.handler.AbstractMessageHandler;

/**
 * Provides message queues and message objects based on an event identifier.
 */
public interface MessageQueueFactory {

    /**
     * Get a queue for a given event identifier.
     * @param eventClass event identifier
     * @return queue id
     */
    String getQueueForName(String eventClass);

    /**
     * Get a message for a given event identifier.
     * @param eventClass event identifier
     * @return message
     */
    GameEventMessage getMessageForName(String eventClass);

    AbstractMessageHandler getHandlerForName(String eventClass);
}
