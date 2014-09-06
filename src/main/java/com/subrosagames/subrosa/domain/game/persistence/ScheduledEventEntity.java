package com.subrosagames.subrosa.domain.game.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.event.ScheduledEvent;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Persists a scheduled event.
 */
@Entity
@DiscriminatorValue(ScheduledEventEntity.EVENT_TYPE_SCHEDULED)
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
