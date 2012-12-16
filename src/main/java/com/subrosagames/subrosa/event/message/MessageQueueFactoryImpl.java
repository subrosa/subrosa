package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class MessageQueueFactoryImpl implements MessageQueueFactory {

    @Override
    public String getQueueForName(String eventClass) {
        return EventMessage.valueOf(eventClass).getQueue();
    }

    @Override
    public AbstractMessage getMessageForName(String eventClass) {
        return EventMessage.valueOf(eventClass).getMessage();
    }
}
