package com.subrosagames.subrosa.event;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.game.event.GameEventMessage;
import com.subrosagames.subrosa.event.handler.AbstractMessageHandler;
import com.subrosagames.subrosa.event.message.MessageQueueFactory;

/**
 * Executes a game event for an event identifier.
 */
@Component
public class EventExecutor {

    /**
     * The method to be called to execute an event.
     */
    public static final String EXECUTE_METHOD = "execute";

    private static final Logger LOG = LoggerFactory.getLogger(EventExecutor.class);

    @Autowired
    private MessageQueueFactory messageQueueFactory;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Execute the event of the specified type for the specified game.
     * @param eventClass event identifier
     * @param gameId game id
     */
    public void execute(String eventClass, int gameId) {
        execute(eventClass, gameId, null);
    }

    /**
     * Execute the event of the specified type for the specified game.
     * @param eventClass event identifier
     * @param gameId game id
     * @param properties event properties
     */
    public void execute(String eventClass, int gameId, Map<String, Serializable> properties) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Firing event {} for game {}", eventClass, gameId);
        }
        GameEventMessage messageForName = messageQueueFactory.getMessageForName(eventClass);
        messageForName.setGameId(gameId);
        messageForName.setProperties(properties);
        AbstractMessageHandler handler = messageQueueFactory.getHandlerForName(eventClass);
        try {
            handler.process(messageForName);
        } catch (Exception e) {
            LOG.error("Exception processing event {}", new Object[] { eventClass, gameId }, e);
        }
    }

    public void executeAsync(String eventClass, int gameId, Map<String, Serializable> properties) {
        String queueForName = messageQueueFactory.getQueueForName(eventClass);
        GameEventMessage messageForName = messageQueueFactory.getMessageForName(eventClass);
        messageForName.setGameId(gameId);
        messageForName.setProperties(properties);
        jmsTemplate.convertAndSend(queueForName, messageForName);
    }

    public void test() {
    }
}