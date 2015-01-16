package com.subrosagames.subrosa.api.dto;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Encapsulates information necessary to join a game.
 */
public class JoinGameRequest {

    private String name;
    private Map<String, Object> attributes = Maps.newHashMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
