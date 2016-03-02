package com.subrosagames.gateway.auth;

/**
 * TODO
 */
public abstract class ConflictException extends Exception {

    public ConflictException() {
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(Throwable cause) {
        super(cause);
    }

    public abstract String getField();
}
