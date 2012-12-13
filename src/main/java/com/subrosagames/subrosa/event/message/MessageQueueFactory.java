package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;

/**
 *
 */
public interface MessageQueueFactory {

    String getQueueForName(String eventClass);

    AbstractMessage getMessageForName(String eventClass);
}
