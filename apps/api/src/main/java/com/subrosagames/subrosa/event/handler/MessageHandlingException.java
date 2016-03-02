package com.subrosagames.subrosa.event.handler;

/**
 * Exception thrown when something goes wrong handling an exception.
 */
public class MessageHandlingException extends Exception {

    /**
     * Default constructor.
     */
    public MessageHandlingException() {
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public MessageHandlingException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause   cause
     */
    public MessageHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     *
     * @param cause cause
     */
    public MessageHandlingException(Throwable cause) {
        super(cause);
    }

}
