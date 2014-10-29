package com.subrosagames.subrosa.domain.game.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;

/**
 * Encapsulates an exception arising from a failed validation of a game.
 */
public class GameEventValidationException extends DomainObjectValidationException {

    private static final long serialVersionUID = 6632349185919738063L;

    /**
     * Default constructor.
     */
    public GameEventValidationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public GameEventValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public GameEventValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public GameEventValidationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with constraint violations.
     *
     * @param violations constraint violations
     */
    public GameEventValidationException(Set<ConstraintViolation<EventEntity>> violations) {
        super(violations);
    }
}

