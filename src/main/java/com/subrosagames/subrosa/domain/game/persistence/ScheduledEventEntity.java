package com.subrosagames.subrosa.domain.game.persistence;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.event.ScheduledEvent;

/**
 *  Persists a scheduled event.
 */
@Entity
@Table(name = "scheduled_event")
@PrimaryKeyJoinColumn(name = "event_id")
public class ScheduledEventEntity extends EventEntity implements ScheduledEvent {

    @Column(name = "event_date")
    private Timestamp eventDate;

    public Timestamp getEventDate() {
        return eventDate == null ? null : new Timestamp(eventDate.getTime());
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate == null ? null : new Timestamp(eventDate.getTime());
    }

}
