package com.subrosagames.gateway.auth;

import lombok.Data;

/**
 * Data structure for requesting a new session.
 */
@Data
public class SessionRequest {
    private String accessToken;
}
