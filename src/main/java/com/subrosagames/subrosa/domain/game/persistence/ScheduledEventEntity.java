package com.subrosagames.subrosa.domain.game.persistence;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;
import com.subrosagames.subrosa.event.message.MessageQueueFactory;
import com.subrosagames.subrosa.event.ScheduledEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jms.core.JmsTemplate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "scheduled_event")
@PrimaryKeyJoinColumn(name = "event_id")
@Configurable
public class ScheduledEventEntity extends EventEntity implements ScheduledEvent {

    @Transient
    @Autowired
    private MessageQueueFactory messageQueueFactory;

    @Transient
    @Autowired
    private JmsTemplate jmsTemplate;

    @Column(name = "event_date")
    private Timestamp eventDate;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public void fire(int gameId) {
        String queueForName = messageQueueFactory.getQueueForName(getEventClass());
        AbstractMessage messageForName = messageQueueFactory.getMessageForName(getEventClass());
        messageForName.setGameId(gameId);
        jmsTemplate.convertAndSend(queueForName, messageForName);
    }
}
