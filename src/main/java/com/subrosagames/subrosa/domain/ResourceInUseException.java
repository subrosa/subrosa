package com.subrosagames.subrosa.domain;

/**
 * Generic domain object in use exception.
 */
public class ResourceInUseException extends Exception {

    private static final long serialVersionUID = 7307154701832308082L;

    /**
     * Default constructor.
     */
    public ResourceInUseException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public ResourceInUseException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public ResourceInUseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public ResourceInUseException(Throwable throwable) {
        super(throwable);
    }

}
