package com.subrosagames.subrosa.domain.game;

/**
 * Represents a piece of information about a player required for enrollment in the game.
 */
public interface EnrollmentField {

    /**
     * Field identifier.
     *
     * @return field id
     */
    String getFieldId();

    /**
     * User-facing field name.
     *
     * @return field name
     */
    String getName();

    /**
     * User-facing field description.
     *
     * @return field description
     */
    String getDescription();

    /**
     * Field type.
     *
     * @return field type
     */
    EnrollmentFieldType getType();

    /**
     * Whether field is required.
     *
     * @return whether field is required
     */
    Boolean getRequired();
}
