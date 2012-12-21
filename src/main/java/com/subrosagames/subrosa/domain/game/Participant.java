package com.subrosagames.subrosa.domain.game;

import java.util.List;

/**
 * Represents a participant in a game.
 */
public interface Participant {

    /**
     * Participant's name.
     * @return name
     */
    String getName();

    List<? extends Target> getTargets();

    Target getTarget(int targetId) throws TargetNotFoundException;
}
