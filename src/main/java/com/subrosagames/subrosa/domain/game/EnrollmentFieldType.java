package com.subrosagames.subrosa.domain.game;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of type that enrollment fields can take.
 */
public enum EnrollmentFieldType {
    /**
     * Simple text field.
     */
    TEXT("text"),
    /**
     * Numeric field.
     */
    NUMBER("number"),
    /**
     * Phone number.
     */
    TEL("tel"),
    /**
     * Image from image gallery.
     */
    IMAGE("image"),
    /**
     * Address from address book.
     */
    ADDRESS("address");

    private final String name;

    /**
     * Construct with string name.
     *
     * @param name field type name
     */
    EnrollmentFieldType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}

