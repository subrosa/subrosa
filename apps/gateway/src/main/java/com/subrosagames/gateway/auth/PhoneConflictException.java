package com.subrosagames.gateway.auth;

/**
 * TODO
 */
public class PhoneConflictException extends ConflictException {

    public PhoneConflictException() {
    }

    public PhoneConflictException(String message) {
        super(message);
    }

    public PhoneConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneConflictException(Throwable cause) {
        super(cause);
    }

    public final String getField() {
        return "phone";
    }

}
