package com.subrosagames.subrosa.api.dto;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.domain.game.EnrollmentField;

/**
 * Transport layer model for player information.
 */
public final class PlayerDescriptor {

    private String name;
    private Map<String, Object> attributes;
    private List<EnrollmentField> enrollmentFields;

    private PlayerDescriptor() { }

    /**
     * Construct with the given list of required enrollment fields.
     *
     * @param enrollmentFields enrollment fields
     * @return player descriptor
     */
    public static PlayerDescriptor forEnrollmentFields(List<EnrollmentField> enrollmentFields) {
        PlayerDescriptor playerDescriptor = new PlayerDescriptor();
        playerDescriptor.enrollmentFields = enrollmentFields;
        return playerDescriptor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Get specified attribute.
     *
     * @param key attribute name
     * @return attribute object
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public List<EnrollmentField> getEnrollmentFields() {
        return enrollmentFields;
    }

}

