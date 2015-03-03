package com.subrosagames.subrosa.domain;

/**
 * Generic domain object not found exception.
 */
public class DomainObjectNotFoundException extends Exception {

    private static final long serialVersionUID = 646960275503767991L;

    /**
     * Default constructor.
     */
    public DomainObjectNotFoundException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public DomainObjectNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s message
     * @param throwable cause
     */
    public DomainObjectNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public DomainObjectNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
