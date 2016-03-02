package com.subrosagames.subrosa.api.dto;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.game.EnrollmentField;

/**
 * Transport layer model for player information.
 */
public final class PlayerDescriptor {

    private PlayerProfile player;
    private Map<String, Object> attributes;
    private List<EnrollmentField> enrollmentFields;

    private PlayerDescriptor() {
    }

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

    public PlayerProfile getPlayer() {
        return player;
    }

    public void setPlayer(PlayerProfile player) {
        this.player = player;
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

