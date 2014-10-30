package com.subrosagames.subrosa.domain.image;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Thrown when a requested image does not exist.
 */
public class ImageNotFoundException extends DomainObjectNotFoundException {

    /**
     * Default constructor.
     */
    public ImageNotFoundException() {
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public ImageNotFoundException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause   cause
     */
    public ImageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     *
     * @param cause cause
     */
    public ImageNotFoundException(Throwable cause) {
        super(cause);
    }

}
