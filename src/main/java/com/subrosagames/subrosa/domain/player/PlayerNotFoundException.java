package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Exception thrown when a requested player does not exist.
 */
public class PlayerNotFoundException extends DomainObjectNotFoundException {

    /**
     * Default constructor.
     */
    public PlayerNotFoundException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public PlayerNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public PlayerNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public PlayerNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
