package com.subrosagames.subrosa.domain.player;

/**
 * Thrown when a requested target does not exist.
 */
public class TargetNotFoundException extends Exception {

    private static final long serialVersionUID = -7956180785472216269L;

    /**
     * Default constructor.
     */
    public TargetNotFoundException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public TargetNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public TargetNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public TargetNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
