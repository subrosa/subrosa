package com.subrosagames.gateway.auth;

/**
 * TODO
 */
public class EmailConflictException extends ConflictException {

    public EmailConflictException() {
    }

    public EmailConflictException(String message) {
        super(message);
    }

    public EmailConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailConflictException(Throwable cause) {
        super(cause);
    }

    public final String getField() {
        return "email";
    }

}
