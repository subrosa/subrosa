package com.subrosagames.subrosa.event;

/**
 * An exception thrown when an event is unable to be scheduled.
 */
public class EventException extends Exception {

    private static final long serialVersionUID = 1301604863966351742L;

    /**
     * Default constructor.
     */
    public EventException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public EventException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public EventException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public EventException(Throwable throwable) {
        super(throwable);
    }
}
