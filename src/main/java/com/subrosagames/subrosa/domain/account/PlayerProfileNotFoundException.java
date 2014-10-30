package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Thrown when requested player profile does not exist.
 */
public class PlayerProfileNotFoundException extends DomainObjectNotFoundException {

    /**
     * Default constructor.
     */
    public PlayerProfileNotFoundException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public PlayerProfileNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s message
     * @param throwable cause
     */
    public PlayerProfileNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public PlayerProfileNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
