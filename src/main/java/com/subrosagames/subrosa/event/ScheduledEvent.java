package com.subrosagames.subrosa.event;

import java.util.Date;

/**
 * {@link Event} that is scheduled to be triggered at a future date.
 */
public interface ScheduledEvent extends Event {

    /**
     * Get the date at which this event should be fired.
     * @return event date
     */
    Date getEventDate();
}
