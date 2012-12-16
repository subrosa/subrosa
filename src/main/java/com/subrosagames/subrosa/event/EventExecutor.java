package com.subrosagames.subrosa.event;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;
import com.subrosagames.subrosa.event.message.MessageQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class EventExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(EventExecutor.class);

    public static final String EXECUTE_METHOD = "execute";

    @Autowired
    private MessageQueueFactory messageQueueFactory;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void execute(String eventClass, int gameId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Firing event {} for game {}", eventClass, gameId);
        }
        String queueForName = messageQueueFactory.getQueueForName(eventClass);
        AbstractMessage messageForName = messageQueueFactory.getMessageForName(eventClass);
        messageForName.setGameId(gameId);
        jmsTemplate.convertAndSend(queueForName, messageForName);
    }
}
