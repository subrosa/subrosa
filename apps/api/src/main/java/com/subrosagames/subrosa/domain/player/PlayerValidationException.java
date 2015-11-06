package com.subrosagames.subrosa.domain.player;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;

/**
 * Exception thrown when player information is invalid.
 */
public class PlayerValidationException extends DomainObjectValidationException {

    /**
     * Default constructor.
     */
    public PlayerValidationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public PlayerValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public PlayerValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public PlayerValidationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with constraint violations.
     *
     * @param violations constraint violations
     */
    public PlayerValidationException(Set<ConstraintViolation<Player>> violations) {
        super(violations);
    }
}
