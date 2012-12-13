package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class MessageQueueFactoryImpl implements MessageQueueFactory {

    @Override
    public String getQueueForName(String eventClass) {
        return eventClass;
    }

    @Override
    public AbstractMessage getMessageForName(String eventClass) {
        return null;
    }
}
