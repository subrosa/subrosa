package com.subrosagames.subrosa.domain.game.persistence;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * Persists a game lifecycle.
 *
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

    @Column(name = "registration_start")
    private Timestamp registrationStart;

    @Column(name = "registration_end")
    private Timestamp registrationEnd;

    @Column(name = "game_start")
    private Timestamp gameStart;

    @Column(name = "game_end")
    private Timestamp gameEnd;

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

    @Override
    public Timestamp getRegistrationStart() {
        return registrationStart == null ? null : new Timestamp(registrationStart.getTime());
    }

    public void setRegistrationStart(Timestamp registrationStart) {
        this.registrationStart = registrationStart == null ? null : new Timestamp(registrationStart.getTime());
    }

    @Override
    public Timestamp getRegistrationEnd() {
        return registrationEnd == null ? null : new Timestamp(registrationEnd.getTime());
    }

    public void setRegistrationEnd(Timestamp registrationEnd) {
        this.registrationEnd = registrationEnd == null ? null : new Timestamp(registrationEnd.getTime());
    }

    @Override
    public Timestamp getGameStart() {
        return gameStart == null ? null : new Timestamp(gameStart.getTime());
    }

    public void setGameStart(Timestamp gameStart) {
        this.gameStart = gameStart == null ? null : new Timestamp(gameStart.getTime());
    }

    @Override
    public Timestamp getGameEnd() {
        return gameEnd == null ? null : new Timestamp(gameEnd.getTime());
    }

    public void setGameEnd(Timestamp gameEnd) {
        this.gameEnd = gameEnd == null ? null : new Timestamp(gameEnd.getTime());
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
    public void addScheduledEvent(EventMessage event, Timestamp time) {
        ScheduledEventEntity scheduledEventEntity = new ScheduledEventEntity();
        scheduledEventEntity.setEventClass(event.getEventClass());
        scheduledEventEntity.setEventType(ScheduledEventEntity.EVENT_TYPE_SCHEDULED);
        scheduledEventEntity.setLifecycle(this);
        scheduledEventEntity.setEventDate(time);
        scheduledEvents.add(scheduledEventEntity);
    }
}
