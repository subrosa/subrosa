package com.subrosagames.subrosa.domain.game.persistence;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

/**
 * Persists a lifecycle for a specific game.
 *
 * Links a game id to a lifecycle id.
 */
@Entity
@Table(name = "game_lifecycle")
@SecondaryTable(name = "lifecycle", pkJoinColumns = @PrimaryKeyJoinColumn(name = "lifecycle_id"))
public class GameLifecycle {

    @Id
    @Column(name = "game_id")
    private Integer gameId;

    @OneToOne
    @JoinColumn(name = "lifecycle_id")
    private LifecycleEntity lifecycleEntity;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public LifecycleEntity getLifecycleEntity() {
        return lifecycleEntity;
    }

    public void setLifecycleEntity(LifecycleEntity lifecycleEntity) {
        this.lifecycleEntity = lifecycleEntity;
    }

    public Timestamp getRegistrationStart() {
        return lifecycleEntity.getRegistrationStart();
    }

    public Timestamp getRegistrationEnd() {
        return lifecycleEntity.getRegistrationEnd();
    }

    public Timestamp getGameStart() {
        return lifecycleEntity.getGameStart();
    }

    public Timestamp getGameEnd() {
        return lifecycleEntity.getGameEnd();
    }

    public List<ScheduledEventEntity> getScheduledEvents() {
        return lifecycleEntity.getScheduledEvents();
    }

    public List<TriggeredEventEntity> getTriggeredEvents() {
        return lifecycleEntity.getTriggeredEvents();
    }

}
