package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.subrosagames.subrosa.domain.game.event.TriggerType;
import com.subrosagames.subrosa.event.TriggeredEvent;

/**
 * Persisted triggered event.
 */
@Entity
@DiscriminatorValue("TRIGGERED")
public class TriggeredEventEntity extends EventEntity implements TriggeredEvent {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_event_id")
    @NotNull
    private EventEntity triggerEvent;

    @Column(name = "trigger_type")
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;

    public EventEntity getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(EventEntity triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }
}
