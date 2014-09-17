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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DiscriminatorOptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;

/**
 * Persists events. Parent class for {@link ScheduledEventEntity}s and {@link TriggeredEventEntity}s.
 */
@Entity
@Table(name = "lifecycle_event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
public abstract class EventEntity extends BaseEntity implements GameEvent {

    @Id
    @SequenceGenerator(name = "lifecycleEventSeq", sequenceName = "lifecycle_event_event_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lifecycleEventSeq")
    @Column(name = "event_id")
    private Integer id;

    @Column(name = "event_class")
    @NotNull
    protected String event;

    @JsonIgnore
    @ManyToOne(targetEntity = GameEntity.class)
    @JoinColumn(name = "game_id")
    private Game game;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

}
