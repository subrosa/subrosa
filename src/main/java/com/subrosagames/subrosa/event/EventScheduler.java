package com.subrosagames.subrosa.event;

import java.util.Collection;

/**
 */
public interface EventScheduler {

    void triggerEvent(TriggeredEvent event, int gameId) throws EventException;

    void scheduleEvent(ScheduledEvent event, int gameId) throws EventException;

    void scheduleEvents(Collection<? extends ScheduledEvent> events, int gameId) throws EventException;
}
