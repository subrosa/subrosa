package com.subrosa.api.notification;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of standard constraint violation notification strings.
 */
public enum NotificationConstraint {

    // CHECKSTYLE-OFF: JavadocVariable
    NOT_UNIQUE("notUnique"),
    UNRECOGNIZED_PROPERTY("unrecognizedProperty");
    // CHECKSTYLE-ON: JavadocVariable

    private final String text;

    /**
     * Constructs with string representation.
     *
     * @param text string representation
     */
    NotificationConstraint(String text) {
        this.text = text;
    }

    /**
     * String representation of this constraint.
     *
     * @return string representation
     */
    @JsonValue
    public String getText() {
        return text;
    }
}