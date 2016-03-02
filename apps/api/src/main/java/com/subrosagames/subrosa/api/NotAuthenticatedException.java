package com.subrosagames.subrosa.api;

/**
 * Exception thrown when caller is not authenticated.
 */
public class NotAuthenticatedException extends Exception {

    private static final long serialVersionUID = 4784534904066131130L;

    /**
     * Default constructor.
     */
    public NotAuthenticatedException() {
        super();
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public NotAuthenticatedException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause   cause
     */
    public NotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     *
     * @param cause cause
     */
    public NotAuthenticatedException(Throwable cause) {
        super(cause);
    }
}
