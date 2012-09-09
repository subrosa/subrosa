package com.subrosagames.subrosa.security;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 8/28/12
 * Time: 2:36 午後
 * To change this template use File | Settings | File Templates.
 */
public class SubrosaSha512PasswordEncoder extends MessageDigestPasswordEncoder {

    public SubrosaSha512PasswordEncoder() throws IllegalArgumentException {
        super("SHA-512", false);
    }

    @Override
    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        return salt + password;
    }
}
