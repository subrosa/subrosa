package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.event.TriggerType;
import com.subrosagames.subrosa.event.TriggeredEvent;

import java.util.Date;

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
