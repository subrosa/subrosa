package com.subrosagames.subrosa.domain.game.event;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Exception thrown when a nonexistent game is retrieved.
 */
public class GameEventNotFoundException extends DomainObjectNotFoundException {


    /**
     * Default constructor.
     */
    public GameEventNotFoundException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public GameEventNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s message
     * @param throwable cause
     */
    public GameEventNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public GameEventNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
