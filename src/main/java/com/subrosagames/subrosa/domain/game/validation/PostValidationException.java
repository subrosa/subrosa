package com.subrosagames.subrosa.domain.game.validation;

import javax.validation.ConstraintViolation;
import java.util.Set;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;

/**
 * Encapsulates an exception arising from a failed validation of a game.
 */
public class PostValidationException extends DomainObjectValidationException {

    /**
     * Default constructor.
     */
    public PostValidationException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public PostValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s         message
     * @param throwable cause
     */
    public PostValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public PostValidationException(Throwable throwable) {
        super(throwable);
    }

    public PostValidationException(Set<ConstraintViolation<PostEntity>> violations) {
        super(violations);
    }
}

