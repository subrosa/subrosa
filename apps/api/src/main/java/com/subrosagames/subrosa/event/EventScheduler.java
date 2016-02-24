package com.subrosagames.subrosa.event;

import java.util.Collection;
import java.util.Date;

import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * Responsible for scheduling and triggering the execution of future events.
 */
public interface EventScheduler {

    /**
     * Schedule the immediate execution of an event for the given game.
     *
     * @param event  event to execute
     * @param gameId game id
     * @throws EventException if event cannot be scheduled
     */
    void triggerEvent(TriggeredEvent event, int gameId) throws EventException;

    /**
     * Schedule the future execution of an {@link EventMessage} for the given game.
     *
     * @param eventMessage event message to schedule
     * @param eventDate    when the event should fire
     * @param gameId       game id
     * @throws EventException if event cannot be scheduled
     */
    void scheduleEvent(EventMessage eventMessage, Date eventDate, int gameId) throws EventException;

    /**
     * Schedule the future execution of a {@link ScheduledEvent}.
     *
     * @param event  event to execute
     * @param gameId game id
     * @throws EventException if event cannot be scheduled
     */
    void scheduleEvent(ScheduledEvent event, int gameId) throws EventException;

    /**
     * Schedule the future execution of multiple {@link ScheduledEvent}s.
     *
     * @param events events to execute
     * @param gameId game id
     * @throws EventException if event cannot be scheduled
     */
    void scheduleEvents(Collection<? extends ScheduledEvent> events, int gameId) throws EventException;
}
