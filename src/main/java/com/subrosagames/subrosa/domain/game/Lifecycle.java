package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.game.persistence.TriggeredEventEntity;
import com.subrosagames.subrosa.event.message.EventMessage;

import java.util.Date;
import java.util.List;

/**
 * Represents the events that occur in a game.
 */
public interface Lifecycle {

    Integer getId();

    Game getGame();

    List<ScheduledEventEntity> getScheduledEvents();

    List<TriggeredEventEntity> getTriggeredEvents();

    void addTriggeredEvent(TriggeredEventEntity triggeredEventEntity);

    void addScheduledEvent(EventMessage event, Date time);
}
