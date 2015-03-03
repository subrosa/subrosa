package com.subrosagames.subrosa.domain.account;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;

/**
 * Thrown when a player profile is invalid for storage.
 */
public class PlayerProfileValidationException extends DomainObjectValidationException {

    /**
     * Default constructor.
     */
    public PlayerProfileValidationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public PlayerProfileValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public PlayerProfileValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public PlayerProfileValidationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with a set of {@link ConstraintViolation}s.
     *
     * @param violations constraint violations
     */
    public PlayerProfileValidationException(Set<ConstraintViolation<PlayerProfile>> violations) {
        super(violations);
    }
}
