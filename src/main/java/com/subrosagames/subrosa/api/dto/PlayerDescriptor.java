package com.subrosagames.subrosa.api.dto;

import java.util.Map;

/**
 * Transport layer model for player information.
 */
public class PlayerDescriptor {

    private String name;
    private Map<String, String> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}

