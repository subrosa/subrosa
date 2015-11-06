package com.subrosagames.subrosa.api.dto;

import com.subrosagames.subrosa.domain.game.EnrollmentField;
import com.subrosagames.subrosa.domain.game.EnrollmentFieldType;
import lombok.Data;

/**
 * Handles deserialization of game enrollment fields.
 */
@Data
public class EnrollmentFieldDto implements EnrollmentField {

    private String fieldId;
    private String name;
    private String description;
    private EnrollmentFieldType type;
    private Boolean required;

}
