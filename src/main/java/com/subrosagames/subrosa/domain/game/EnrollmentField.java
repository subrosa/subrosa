package com.subrosagames.subrosa.domain.game;

/**
 * Represents a piece of information about a player required for enrollment in the game.
 */
public interface EnrollmentField {

    String getFieldId();

    String getName();

    String getDescription();

    EnrollmentFieldType getType();
}
