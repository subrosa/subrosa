package com.subrosagames.subrosa.domain;

/**
 * Generic domain object validation exception.
 */
public class DomainObjectValidationException extends Exception {

    private static final long serialVersionUID = 797174315554348934L;

    /**
     * Default constructor.
     */
    public DomainObjectValidationException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public DomainObjectValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s message
     * @param throwable cause
     */
    public DomainObjectValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public DomainObjectValidationException(Throwable throwable) {
        super(throwable);
    }
}
