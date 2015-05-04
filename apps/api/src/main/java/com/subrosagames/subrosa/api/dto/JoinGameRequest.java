package com.subrosagames.subrosa.api.dto;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Encapsulates information necessary to join a game.
 */
public class JoinGameRequest {

    private Integer playerId;
    private Map<String, Object> attributes = Maps.newHashMap();

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
