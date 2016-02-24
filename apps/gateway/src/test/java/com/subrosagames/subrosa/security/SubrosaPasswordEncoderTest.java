package com.subrosagames.subrosa.security;

import org.junit.Test;

import com.subrosagames.gateway.auth.SubrosaPasswordEncoder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test {@link SubrosaPasswordEncoder}.
 */
public class SubrosaPasswordEncoderTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private SubrosaPasswordEncoder encoder = new SubrosaPasswordEncoder();

    @Test
    public void testPasswordEncoding() throws Exception {
        String encodedPassword = encoder.encodePassword("password", "salt");
        assertTrue(encoder.isPasswordValid(encodedPassword, "password", "salt"));
        assertFalse(encoder.isPasswordValid(encodedPassword, "password", "wrong salt"));
        assertFalse(encoder.isPasswordValid(encodedPassword, "not right", "salt"));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
