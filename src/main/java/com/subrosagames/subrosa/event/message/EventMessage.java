package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;

/**
 * Enumeration of supported event-based JMS messages.
 *
 * Each supported event has an associated message queue name and type of message expected to be sent on it.
 */
public enum EventMessage {

    /**
     * Triggered upon the start of a game.
     */
    GAME_START("subrosa.game.start", StartGameMessage.class),
    /**
     * Triggered at the end of a game.
     */
    GAME_END("subrosa.game.end", EndGameMessage.class),
    TARGET_ACHIEVED("subrosa.game.target.achieved", TargetAchievedMessage.class);

    private final String queue;
    private final Class<? extends AbstractMessage> messageClass;

    /**
     * Construct with queue and message class.
     * @param queue message queue
     * @param messageClass type of message to be sent
     */
    EventMessage(String queue, Class<? extends AbstractMessage> messageClass) {
        this.queue = queue;
        this.messageClass = messageClass;
    }

    /**
     * Get the message queue for this event.
     * @return message queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * Get an message instance to be sent on the message queue.
     * @return instantiated message
     */
    public AbstractMessage getMessage() { // SUPPRESS CHECKSTYLE IllegalType
        try {
            return messageClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Uninstantiable game event message " + messageClass + " encountered", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Uninstantiable game event message " + messageClass + " encountered", e);
        }
    }
}
