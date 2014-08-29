package com.subrosagames.subrosa.domain.game.persistence;

import com.subrosagames.subrosa.event.ScheduledEvent;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Persists a scheduled event.
 */
@Entity
@Table(name = "scheduled_event")
@DiscriminatorValue(ScheduledEventEntity.EVENT_TYPE_SCHEDULED)
@PrimaryKeyJoinColumn(name = "event_id")
public class ScheduledEventEntity extends EventEntity implements ScheduledEvent {

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
