package com.subrosagames.subrosa.domain.game;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A player attribute that must be set to play in a game.
 */
public interface RequiredAttribute {

    /**
     * Player attribute name.
     * @return attribute name
     */
    @JsonValue
    String getName();
}
