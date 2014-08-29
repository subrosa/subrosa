package com.subrosagames.subrosa.domain.game.persistence;

import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;

import javax.persistence.*;

/**
 * Persists events. Parent class for {@link ScheduledEventEntity}s and {@link TriggeredEventEntity}s.
 */
@Entity
@Table(name = "lifecycle_event")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
public class EventEntity extends BaseEntity implements GameEvent {

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
