package com.subrosagames.subrosa.domain.player;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Player validation involving insufficient user information.
 */
public class PlayRestrictedException extends PlayerValidationException {

    /**
     * Default constructor.
     */
    public PlayRestrictedException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public PlayRestrictedException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public PlayRestrictedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public PlayRestrictedException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with constraint violations.
     *
     * @param violations constraint violations
     */
    public PlayRestrictedException(Set<ConstraintViolation<PlayerEntity>> violations) {
        super(violations);
    }
}
