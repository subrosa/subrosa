package com.subrosagames.subrosa.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

/**
 * Provides functionality related to the handling of passwords.
 */
@Component
public class PasswordUtility {

    private static final int SALT_LENGTH = 64;

    /**
     * Encrypt the provided password using base64-encoded salted SHA-512.
     *
     * @param plaintext plaintext password
     * @return encrypted password
     */
    public String encryptPassword(String plaintext) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System does not support SHA-512");
        }

        // encrypt salt + plaintext
        char[] salt = Hex.encode(generateSalt());
        try {
            digest.update(new String(salt).getBytes("UTF-8"));
            digest.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not encrypt password using UTF-8 encoding.", e);
        }
        return new String(salt) + new String(Hex.encode(digest.digest()));
    }

    private byte[] generateSalt() {
        Random random = new SecureRandom();
        byte[] bytes = new byte[SALT_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

}
