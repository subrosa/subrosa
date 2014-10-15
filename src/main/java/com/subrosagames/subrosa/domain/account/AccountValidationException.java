package com.subrosagames.subrosa.domain.account;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;

/**
 * Exception indicating one or more parts of an account object are invalid.
 */
public class AccountValidationException extends DomainObjectValidationException {

    private static final long serialVersionUID = -65352831152690754L;

    /**
     * Default constructor.
     */
    public AccountValidationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public AccountValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public AccountValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public AccountValidationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with a set of {@link ConstraintViolation}s.
     *
     * @param violations constraint violations
     */
    public AccountValidationException(Set<ConstraintViolation<Account>> violations) {
        super(violations);
    }
}
