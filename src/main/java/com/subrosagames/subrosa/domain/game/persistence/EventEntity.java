package com.subrosagames.subrosa.domain.game.persistence;

import com.subrosagames.subrosa.domain.game.Lifecycle;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = LifecycleEntity.class)
    @JoinColumn(name = "lifecycle_id", referencedColumnName = "game_id")
    private Lifecycle lifecycle;

    @Column(name = "event_class")
    private String eventClass;

    @Column(name = "event_type")
    private String eventType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
