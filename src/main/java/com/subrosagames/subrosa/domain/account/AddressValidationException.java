package com.subrosagames.subrosa.domain.account;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;

/**
 * Exception indicating one or more parts of an address object are invalid.
 */
public class AddressValidationException extends DomainObjectValidationException {

    private static final long serialVersionUID = -8281563470563130771L;

    /**
     * Default constructor.
     */
    public AddressValidationException() {
    }

    /**
     * Construct with message.
     *
     * @param s message
     */
    public AddressValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     *
     * @param s         message
     * @param throwable cause
     */
    public AddressValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     *
     * @param throwable cause
     */
    public AddressValidationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Construct with a set of {@link ConstraintViolation}s.
     *
     * @param violations constraint violations
     */
    public AddressValidationException(Set<ConstraintViolation<Address>> violations) {
        super(violations);
    }
}
