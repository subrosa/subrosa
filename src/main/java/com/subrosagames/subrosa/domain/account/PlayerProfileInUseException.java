package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.domain.ResourceInUseException;

/**
 * Exception thrown when an attempt to delete a player profile that is still in use occurs.
 */
public class PlayerProfileInUseException extends ResourceInUseException {

    /**
     * Default constructor.
     */
    public PlayerProfileInUseException() {
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public PlayerProfileInUseException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause cause
     */
    public PlayerProfileInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     *
     * @param cause cause
     */
    public PlayerProfileInUseException(Throwable cause) {
        super(cause);
    }

}
