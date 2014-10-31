package com.subrosagames.subrosa.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test {@link SubrosaSha512PasswordEncoder}.
 */
public class SubrosaSha512PasswordEncoderTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private SubrosaSha512PasswordEncoder encoder = new SubrosaSha512PasswordEncoder();

    @Test
    public void testPasswordEncoding() throws Exception {
        String encodedPassword = encoder.encodePassword("password", "salt");
        assertTrue(encoder.isPasswordValid(encodedPassword, "password", "salt"));
        assertFalse(encoder.isPasswordValid(encodedPassword, "password", "wrong salt"));
        assertFalse(encoder.isPasswordValid(encodedPassword, "not right", "salt"));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
