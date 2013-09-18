package com.subrosagames.subrosa.api;

/**
 */
public class NotAuthenticatedException extends Exception {

    private static final long serialVersionUID = 4784534904066131130L;

    public NotAuthenticatedException() {
        super();
    }

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthenticatedException(Throwable cause) {
        super(cause);
    }
}
