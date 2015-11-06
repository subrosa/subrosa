package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Exception thrown when a requested team does not exist.
 */
public class TeamNotFoundException extends DomainObjectNotFoundException {

    /**
     * Default constructor.
     */
    public TeamNotFoundException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public TeamNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public TeamNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public TeamNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
