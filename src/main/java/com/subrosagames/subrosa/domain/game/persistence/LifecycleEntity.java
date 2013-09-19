package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.event.message.EventMessage;
import com.google.common.collect.Lists;

/**
 * Persists a game lifecycle.
 * <p/>
 * Consists of registration times, game start and end, and scheduled and triggered events.
 */
@Entity
@Table(name = "lifecycle")
public class LifecycleEntity implements Lifecycle {

    @Id
    @SequenceGenerator(name = "lifecycleSeq", sequenceName = "lifecycle_lifecycle_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lifecycleSeq")
    @Column(name = "lifecycle_id")
    private Integer id;

    @OneToOne(targetEntity = GameEntity.class)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany
    @JoinColumn(name = "event_id")
    private List<ScheduledEventEntity> scheduledEvents = Lists.newArrayList();

    @OneToMany
    @JoinColumn(name = "event_id")
    private List<TriggeredEventEntity> triggeredEvents = Lists.newArrayList();

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public List<ScheduledEventEntity> getScheduledEvents() {
        return scheduledEvents;
    }

    public void setScheduledEvents(List<ScheduledEventEntity> scheduledEvents) {
        this.scheduledEvents = scheduledEvents;
    }

    @Override
    public List<TriggeredEventEntity> getTriggeredEvents() {
        return triggeredEvents;
    }

    public void setTriggeredEvents(List<TriggeredEventEntity> triggeredEvents) {
        this.triggeredEvents = triggeredEvents;
    }

    @Override
    public void addTriggeredEvent(TriggeredEventEntity triggeredEventEntity) {
        triggeredEvents.add(triggeredEventEntity);
    }

    @Override
    public void addScheduledEvent(EventMessage event, Date time) {
        ScheduledEventEntity scheduledEventEntity = new ScheduledEventEntity();
        scheduledEventEntity.setEventClass(event.getEventClass());
        scheduledEventEntity.setEventType(ScheduledEventEntity.EVENT_TYPE_SCHEDULED);
        scheduledEventEntity.setLifecycle(this);
        scheduledEventEntity.setEventDate(time);
        scheduledEvents.add(scheduledEventEntity);
    }
}
