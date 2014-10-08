package com.subrosagames.subrosa.domain.token;

/**
 * Thrown when a token is not valid for the attempted use.
 */
public class TokenInvalidException extends Exception {

    /**
     * Default constructor.
     */
    public TokenInvalidException() {
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public TokenInvalidException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause cause
     */
    public TokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     * @param cause cause
     */
    public TokenInvalidException(Throwable cause) {
        super(cause);
    }
}
