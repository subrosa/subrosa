package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Exception thrown when a nonexistent game is retrieved.
 */
public class GameNotFoundException extends DomainObjectNotFoundException {

    private static final long serialVersionUID = -574390162601235389L;

    /**
     * Default constructor.
     */
    public GameNotFoundException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public GameNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public GameNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public GameNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
