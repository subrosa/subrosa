package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.domain.ResourceInUseException;

/**
 * Exception thrown when an attempt to delete an image that is still in use occurs.
 */
public class ImageInUseException extends ResourceInUseException {

    /**
     * Default constructor.
     */
    public ImageInUseException() {
    }

    /**
     * Construct with message.
     *
     * @param message message
     */
    public ImageInUseException(String message) {
        super(message);
    }

    /**
     * Construct with message and cause.
     *
     * @param message message
     * @param cause   cause
     */
    public ImageInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct with cause.
     *
     * @param cause cause
     */
    public ImageInUseException(Throwable cause) {
        super(cause);
    }

}
