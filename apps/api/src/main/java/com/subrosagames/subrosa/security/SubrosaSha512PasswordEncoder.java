package com.subrosagames.subrosa.security;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

/**
 * Overrides the default spring security salted password encoder to manipulate salt handling.
 */
public class SubrosaSha512PasswordEncoder extends MessageDigestPasswordEncoder {

    /**
     * Constructs as a SHA-512 message digest encoder.
     */
    public SubrosaSha512PasswordEncoder() {
        super("SHA-512", false);
    }

    @Override
    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        return salt + password;
    }
}
