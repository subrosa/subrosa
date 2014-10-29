package com.subrosagames.subrosa.domain.game.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.game.BaseGame;

/**
 * Encapsulates an exception arising from a failed validation of a game.
 */
public class GameValidationException extends DomainObjectValidationException {

    private static final long serialVersionUID = 5619171923027424186L;

    /**
     * Default constructor.
     */
    public GameValidationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public GameValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public GameValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public GameValidationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with the specified constraint violations.
     *
     * @param violations constraint violations
     */
    public GameValidationException(Set<ConstraintViolation<BaseGame>> violations) {
        super(violations);
    }
}
