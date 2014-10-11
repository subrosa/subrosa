package com.subrosagames.subrosa.domain.player;

import java.util.Map;

/**
 * Model for Players.
 */
public interface Player extends Participant {

    /**
     * Get player id.
     *
     * @return player id
     */
    Integer getId();

    /**
     * Get player name.
     *
     * @return player name
     */
    String getName();

    /**
     * Get player attributes.
     *
     * @return player attributes
     */
    Map<String, String> getAttributes();

}
