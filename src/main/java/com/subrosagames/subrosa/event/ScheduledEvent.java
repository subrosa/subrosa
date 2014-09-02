package com.subrosagames.subrosa.event;

import com.subrosagames.subrosa.domain.game.event.GameEvent;

import java.util.Date;

/**
 * {@link Event} that is scheduled to be triggered at a future date.
 */
public interface ScheduledEvent extends GameEvent {

    /**
     * Get the date at which this event should be fired.
     * @return event date
     */
    Date getDate();
}
