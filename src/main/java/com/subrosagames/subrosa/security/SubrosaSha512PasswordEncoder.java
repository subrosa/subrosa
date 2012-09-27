package com.subrosagames.subrosa.security;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

/**
 * Encode passwords with salted SHA-512.
 */
public class SubrosaSha512PasswordEncoder extends MessageDigestPasswordEncoder {

    /**
     * Encode a password in SHA-512.
     * @throws IllegalArgumentException for illegal arguments to super().
     */
    public SubrosaSha512PasswordEncoder() throws IllegalArgumentException {
        super("SHA-512", false);
    }

    @Override
    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        return salt + password;
    }
}
