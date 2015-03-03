package com.subrosagames.subrosa.api;

/**
 * Encapsulates request for activating an account.
 */
public class AccountActivation {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
