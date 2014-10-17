package com.subrosagames.subrosa.api.dto;

import com.subrosagames.subrosa.domain.game.EnrollmentField;
import com.subrosagames.subrosa.domain.game.EnrollmentFieldType;

/**
 * Handles deserialization of game enrollment fields.
 */
public class EnrollmentFieldDto implements EnrollmentField {

    private String fieldId;
    private String name;
    private String description;
    private EnrollmentFieldType type;

    @Override
    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public EnrollmentFieldType getType() {
        return type;
    }

    public void setType(EnrollmentFieldType type) {
        this.type = type;
    }
}
