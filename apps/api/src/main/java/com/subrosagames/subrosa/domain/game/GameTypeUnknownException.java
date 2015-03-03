package com.subrosagames.subrosa.domain.game;

/**
 * Thrown when an invalid @{link GameType} is used.
 */
public class GameTypeUnknownException extends Exception {

    /**
     * Default constructor.
     */
    public GameTypeUnknownException() {
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public GameTypeUnknownException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause cause
     */
    public GameTypeUnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     *
     * @param cause cause
     */
    public GameTypeUnknownException(Throwable cause) {
        super(cause);
    }

}

