package com.subrosagames.subrosa.domain.game.persistence;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Entity
@Table(name = "lifecycle")
public class Lifecycle {

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Timestamp registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Timestamp getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Timestamp registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public Timestamp getGameStart() {
        return gameStart;
    }

    public void setGameStart(Timestamp gameStart) {
        this.gameStart = gameStart;
    }

    public Timestamp getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(Timestamp gameEnd) {
        this.gameEnd = gameEnd;
    }

    public List<ScheduledEventEntity> getScheduledEvents() {
        return scheduledEvents;
    }

    public void setScheduledEvents(List<ScheduledEventEntity> scheduledEvents) {
        this.scheduledEvents = scheduledEvents;
    }

    public List<TriggeredEventEntity> getTriggeredEvents() {
        return triggeredEvents;
    }

    public void setTriggeredEvents(List<TriggeredEventEntity> triggeredEvents) {
        this.triggeredEvents = triggeredEvents;
    }
}
