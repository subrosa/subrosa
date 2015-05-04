package com.subrosagames.subrosa.domain.account;

/**
 * Exception thrown when a nonexistent account is retrieved.
 */
public class EmailConflictException extends AccountValidationException {


    private static final long serialVersionUID = -7739820652337308066L;

    /**
     * Default constructor.
     */
    public EmailConflictException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public EmailConflictException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public EmailConflictException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public EmailConflictException(Throwable throwable) {
        super(throwable);
    }
}
