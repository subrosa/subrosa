package com.subrosagames.subrosa.domain.game.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
