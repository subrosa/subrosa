package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Persisted triggered event.
 */
@Entity
@Table(name = "triggered_event")
@PrimaryKeyJoinColumn(name = "event_id")
public class TriggeredEventEntity extends EventEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_event_id", nullable = false)
    private EventEntity triggerEvent;

    @Column(name = "trigger_type")
    @Enumerated(EnumType.STRING)
    private String triggerType;

    public EventEntity getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(EventEntity triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
}
