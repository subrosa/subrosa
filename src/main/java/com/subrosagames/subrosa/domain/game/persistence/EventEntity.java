package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Persists events. Parent class for {@link ScheduledEventEntity}s and {@link TriggeredEventEntity}s.
 */
@Entity
@Table(name = "lifecycle_event")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
public class EventEntity {

    @Id
    @SequenceGenerator(name = "lifecycleEventSeq", sequenceName = "lifecycle_event_event_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lifecycleEventSeq")
    @Column(name = "event_id")
    private Integer id;

    @Column(name = "event_class")
    private String eventClass;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }
}
