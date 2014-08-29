package com.subrosagames.subrosa.api;

/**
 * Exception thrown when caller is not authenticated.
 */
public class NotAuthorizedException extends Exception {

    private static final long serialVersionUID = 4784534904066131130L;

    /**
     * Default constructor.
     */
    public NotAuthorizedException() {
        super();
    }

    /**
     * Construct with message.
     * @param message message
     */
    public NotAuthorizedException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     * @param message message
     * @param cause cause
     */
    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     * @param cause cause
     */
    public NotAuthorizedException(Throwable cause) {
        super(cause);
    }
}
