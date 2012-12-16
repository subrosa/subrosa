package com.subrosagames.subrosa.event;

import com.subrosagames.subrosa.event.message.EventMessage;

import java.util.Collection;
import java.util.Date;

/**
 */
public interface EventScheduler {

    void triggerEvent(TriggeredEvent event, int gameId) throws EventException;

    void scheduleEvent(EventMessage eventMessage, Date eventDate, int gameId) throws EventException;

    void scheduleEvent(ScheduledEvent event, int gameId) throws EventException;

    void scheduleEvents(Collection<? extends ScheduledEvent> events, int gameId) throws EventException;
}
