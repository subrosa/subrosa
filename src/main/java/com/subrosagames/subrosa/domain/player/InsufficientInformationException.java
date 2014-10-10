package com.subrosagames.subrosa.domain.player;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Player validation involving insufficient user information.
 */
public class InsufficientInformationException extends PlayerValidationException {

    /**
     * Default constructor.
     */
    public InsufficientInformationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public InsufficientInformationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public InsufficientInformationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public InsufficientInformationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with constraint violations.
     *
     * @param violations constraint violations
     */
    public InsufficientInformationException(Set<ConstraintViolation<PlayerEntity>> violations) {
        super(violations);
    }
}
