package com.subrosagames.subrosa.domain.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Sparse implementation of a constraint violation allowing for virtual field violations.
 *
 * @param <T> type being validated
 * @see VirtualConstraintPath
 */
public class VirtualConstraintViolation<T> implements ConstraintViolation<T> {

    private String message;
    private String path;

    /**
     * Construct with the given message and field name.
     *
     * @param message message
     * @param path    field name
     */
    public VirtualConstraintViolation(String message, String path) {
        this.message = message;
        this.path = path;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Path getPropertyPath() {
        return new VirtualConstraintPath(path);
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public T getRootBean() {
        return null;
    }

    @Override
    public Class<T> getRootBeanClass() {
        return null;
    }

    @Override
    public Object getLeafBean() {
        return null;
    }

    @Override
    public Object[] getExecutableParameters() {
        return new Object[0];
    }

    @Override
    public Object getExecutableReturnValue() {
        return null;
    }

    @Override
    public Object getInvalidValue() {
        return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
    }

    @Override
    public <U> U unwrap(Class<U> type) {
        return null;
    }
}
