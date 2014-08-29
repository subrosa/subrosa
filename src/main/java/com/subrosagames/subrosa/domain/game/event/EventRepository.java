package com.subrosagames.subrosa.domain.game.event;

import com.subrosagames.subrosa.domain.game.persistence.TriggeredEventEntity;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 *
 */
public interface EventRepository {

    TriggeredEventEntity createTriggeredEvent(EventMessage eventMessage, Event trigger);
}
