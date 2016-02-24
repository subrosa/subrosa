package com.subrosagames.gateway.auth;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * Encapsulates information for registering a new account.
 * <p>
 * This includes an account identifier (either email or phone number) and a password for authenticating with that account.
 */
@Data
public class RegistrationRequest {

    private String email;
    private String phone;
    @NotEmpty
    private String password;
}
