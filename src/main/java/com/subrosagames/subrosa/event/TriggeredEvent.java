package com.subrosagames.subrosa.event;

import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;

/**
 * TODO.
 */
public interface TriggeredEvent extends Event {

    public GameEvent getTriggerEvent();

}
