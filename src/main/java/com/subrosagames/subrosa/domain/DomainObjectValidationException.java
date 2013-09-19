package com.subrosagames.subrosa.domain;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic domain object validation exception.
 */
public abstract class DomainObjectValidationException extends Exception {

    private static final long serialVersionUID = 797174315554348934L;

    private Set<? extends ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();

    /**
     * Default constructor.
     */
    public DomainObjectValidationException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public DomainObjectValidationException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s         message
     * @param throwable cause
     */
    public DomainObjectValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public DomainObjectValidationException(Throwable throwable) {
        super(throwable);
    }

    public DomainObjectValidationException(Set<? extends ConstraintViolation<?>> violations) {
        this.violations = violations;
    }

    public Set<? extends ConstraintViolation<?>> getViolations() {
        return violations;
    }
}