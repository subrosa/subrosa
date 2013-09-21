package com.subrosagames.subrosa.event.message;

import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.game.event.GameEventMessage;
import com.subrosagames.subrosa.event.handler.AbstractMessageHandler;

/**
 * Provides queues and messages based on the {@link EventMessage} enumeration of supported event types.
 *
 * @see EventMessage
 */
@Component
public class MessageQueueFactoryImpl implements MessageQueueFactory {

    @Override
    public String getQueueForName(String eventClass) {
        return EventMessage.valueOf(eventClass).getQueue();
    }

    @Override
    public GameEventMessage getMessageForName(String eventClass) {
        return EventMessage.valueOf(eventClass).getMessage();
    }

    @Override
    public AbstractMessageHandler getHandlerForName(String eventClass) { // SUPPRESS CHECKSTYLE IllegalType
        return EventMessage.valueOf(eventClass).getHandler();
    }
}
