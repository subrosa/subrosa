package com.subrosagames.subrosa.domain.player;

/**
 * An achievable target for a player.
 */
public interface Target {

    /**
     * Get target id.
     *
     * @return target id
     */
    Integer getId();

    /**
     * Get target type.
     *
     * @return target type
     */
    TargetType getTargetType();

}
