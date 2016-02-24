package com.subrosagames.gateway.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Overrides the default spring security salted password encoder to manipulate salt handling.
 */
@Component
public class SubrosaPasswordEncoder extends MessageDigestPasswordEncoder implements PasswordEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaPasswordEncoder.class);

    private BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    /**
     * Constructs as a SHA-512 message digest encoder.
     */
    public SubrosaPasswordEncoder() {
        super("SHA-512", false);
    }

    @Override
    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        return salt + password;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (isLegacyMatch(rawPassword, encodedPassword)) {
            // TODO update password
            return true;
        }
        return bcryptEncoder.matches(rawPassword, encodedPassword);
    }

    private boolean isLegacyMatch(CharSequence rawPassword, String encodedPassword) {
        LOG.debug("Checking for legacy password match against {}", encodedPassword);
        if (encodedPassword.startsWith("$")) {
            LOG.debug("Looks like a BCrypt password");
            // modern bcrypt passwords
            return false;
        } else if (encodedPassword.length() < 128) {
            LOG.debug("Looks like a really old password");
            // TODO these are the really old passwords - any use trying to migrate? should probably force a password reset instead
            return false;
        }
        String encoded = encodedPassword.substring(128);
        String salt = encodedPassword.substring(0, 128);
        LOG.debug("Password portion: {} | Salt portion: {}, | Encoded input: {}", encoded, salt, encodePassword(rawPassword.toString(), salt));
        return isPasswordValid(encoded, rawPassword.toString(), salt);
    }

}
