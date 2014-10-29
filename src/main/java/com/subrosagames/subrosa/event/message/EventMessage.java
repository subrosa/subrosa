package com.subrosagames.subrosa.event.message;

import org.springframework.context.ApplicationContext;

import com.subrosagames.subrosa.domain.game.event.GameEventMessage;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.handler.AbstractMessageHandler;
import com.subrosagames.subrosa.event.handler.GameEndMessageHandler;
import com.subrosagames.subrosa.event.handler.GameStartMessageHandler;
import com.subrosagames.subrosa.event.handler.MutualInterestAssignmentMessageHandler;
import com.subrosagames.subrosa.event.handler.RoundRobinAssignmentMessageHandler;
import com.subrosagames.subrosa.infrastructure.spring.ApplicationContextProvider;

/**
 * Enumeration of supported event-based JMS messages.
 * <p/>
 * Each supported event has an associated message queue name and type of message expected to be sent on it.
 */
public enum EventMessage implements Event {

    // CHECKSTYLE-OFF: JavadocVariable

    GAME_START("subrosa.game.start", GameEventMessage.class, GameStartMessageHandler.class),
    GAME_END("subrosa.game.end", GameEventMessage.class, GameEndMessageHandler.class),
    TARGET_ACHIEVED("subrosa.game.target.achieved", GameEventMessage.class, null),
    ROUND_ROBIN_ASSIGNMENT(null, GameEventMessage.class, RoundRobinAssignmentMessageHandler.class),
    MUTUAL_INTEREST_ASSIGNMENT(null, GameEventMessage.class, MutualInterestAssignmentMessageHandler.class);

    // CHECKSTYLE-ON: JavadocVariable

    private final String queue;
    private final Class<? extends GameEventMessage> messageClass;
    private final Class<? extends AbstractMessageHandler> handlerClass;

    /**
     * Construct with queue and message class.
     *
     * @param queue        message queue
     * @param messageClass type of message to be sent
     * @param handlerClass type of handler for event
     */
    EventMessage(String queue, Class<? extends GameEventMessage> messageClass, Class<? extends AbstractMessageHandler> handlerClass) {
        this.queue = queue;
        this.messageClass = messageClass;
        this.handlerClass = handlerClass;
    }

    /**
     * Get the message queue for this event.
     *
     * @return message queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * Get an message instance to be sent on the message queue.
     *
     * @return instantiated message
     */
    public GameEventMessage getMessage() {
        try {
            return messageClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Uninstantiable game event message " + messageClass + " encountered", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Uninstantiable game event message " + messageClass + " encountered", e);
        }
    }

    public AbstractMessageHandler getHandler() { // SUPPRESS CHECKSTYLE IllegalType
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(handlerClass);
    }

    @Override
    public String getEvent() {
        return name();
    }

}
