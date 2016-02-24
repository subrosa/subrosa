package com.subrosagames.gateway.auth;

import lombok.Data;

/**
 * Encapsulates data necessary to request a new session.
 */
@Data
public class LoginRequest {
    private String email;
    private String phone;
    private String password;
}
