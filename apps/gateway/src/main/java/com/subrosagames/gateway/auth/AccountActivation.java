package com.subrosagames.gateway.auth;

import lombok.Data;

/**
 * Encapsulates request for activating an account.
 */
@Data
public class AccountActivation {
    private String token;
}
