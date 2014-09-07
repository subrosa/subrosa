package com.subrosa.api.exception;

/**
 * Exception thrown when an object is requested but could not be found.
 */
public class NotFoundException extends Exception {

    static final long serialVersionUID = -7294227153290083870L;

    private static final String FILE_NOT_FOUND_WITH_ID = "Unable to locate file with id %s";

    /**
     * Construct exception with message pertaining to given id.
     *
     * @param fileId file id
     */
    public NotFoundException(Long fileId) {
        super(String.format(FILE_NOT_FOUND_WITH_ID, fileId));
    }

    /**
     * Construct exception with no message.
     */
    public NotFoundException() {
        super();
    }

    /**
     * Construct exception with message.
     *
     * @param message message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Construct exception with message and cause.
     *
     * @param message message
     * @param cause cause
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct exception with cause.
     *
     * @param cause cause
     */
    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
