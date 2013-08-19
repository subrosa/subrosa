package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.*;

import com.subrosagames.subrosa.domain.game.event.TriggerType;
import com.subrosagames.subrosa.event.TriggeredEvent;

/**
 * Persisted triggered event.
 */
@Entity
@Table(name = "triggered_event")
@DiscriminatorValue("TRIGGERED")
@PrimaryKeyJoinColumn(name = "event_id")
public class TriggeredEventEntity extends EventEntity implements TriggeredEvent {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_event_id", nullable = false)
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
