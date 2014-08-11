package com.subrosagames.subrosa.api;

/**
 * Exception thrown when caller is not authenticated.
 */
public class BadRequestException extends Exception {

    /**
     * Default constructor.
     */
    public BadRequestException() {
        super();
    }

    /**
     * Construct with message.
     * @param message message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     * @param message message
     * @param cause cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     * @param cause cause
     */
    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
