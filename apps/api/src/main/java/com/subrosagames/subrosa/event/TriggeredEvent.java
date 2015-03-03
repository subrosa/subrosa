package com.subrosagames.subrosa.event;

import com.subrosagames.subrosa.domain.game.event.GameEvent;

/**
 * Event triggered by another event.
 */
public interface TriggeredEvent extends Event {

    /**
     * Get event trigger.
     *
     * @return event trigger
     */
    GameEvent getTriggerEvent();

}
