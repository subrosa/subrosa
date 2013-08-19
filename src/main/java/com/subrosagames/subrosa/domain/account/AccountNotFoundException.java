package com.subrosagames.subrosa.domain.account;

/**
 * Exception thrown when a nonexistent account is retrieved.
 */
public class AccountNotFoundException extends Exception {

    private static final long serialVersionUID = 4438876198543992287L;

    /**
     * Default constructor.
     */
    public AccountNotFoundException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public AccountNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s message
     * @param throwable cause
     */
    public AccountNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public AccountNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
