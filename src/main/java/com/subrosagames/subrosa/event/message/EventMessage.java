package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;

/**
 *
 */
public enum EventMessage {

    GAME_START("subrosa.game.start", StartGameMessage.class),
    GAME_END("subrosa.game.end", EndGameMessage.class)
    ;

    private final String queue;
    private final Class<? extends AbstractMessage> messageClass;

    EventMessage(String queue, Class<? extends AbstractMessage> messageClass) {
        this.queue = queue;
        this.messageClass = messageClass;
    }

    public String getQueue() {
        return queue;
    }

    public AbstractMessage getMessage() {
        try {
            return messageClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Uninstantiable game event message " + messageClass + " encountered", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Uninstantiable game event message " + messageClass + " encountered", e);
        }
    }
}
