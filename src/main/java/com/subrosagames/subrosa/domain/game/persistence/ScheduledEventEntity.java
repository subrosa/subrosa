package com.subrosagames.subrosa.domain.game.persistence;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.subrosagames.subrosa.event.ScheduledEvent;

/**
 * Persists a scheduled event.
 */
@Entity
@DiscriminatorValue(ScheduledEventEntity.EVENT_TYPE_SCHEDULED)
public class ScheduledEventEntity extends EventEntity implements ScheduledEvent {

    /**
     * Discriminator type.
     */
    public static final String EVENT_TYPE_SCHEDULED = "SCHEDULED";

    @Column(name = "event_date")
    @NotNull
    private Date date;

    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());
    }

}
