package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.DomainObject;

/**
 * An achievable target for a player.
 */
public interface Target extends DomainObject {

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
