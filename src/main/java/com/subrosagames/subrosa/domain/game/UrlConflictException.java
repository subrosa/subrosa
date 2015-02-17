package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.validation.GameValidationException;

/**
 * Exception thrown when a nonexistent account is retrieved.
 */
public class UrlConflictException extends GameValidationException {

    private static final long serialVersionUID = 4619398077691811192L;

    /**
     * Default constructor.
     */
    public UrlConflictException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public UrlConflictException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public UrlConflictException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public UrlConflictException(Throwable throwable) {
        super(throwable);
    }
}
