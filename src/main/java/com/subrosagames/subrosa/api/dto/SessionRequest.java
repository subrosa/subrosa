package com.subrosagames.subrosa.api.dto;

/**
 * Data structure for requesting a new session.
 */
public class SessionRequest {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
