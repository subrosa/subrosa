package com.subrosagames.subrosa.security;

import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Provides functionality related to the handling of passwords.
 */
@Component
public class PasswordUtility {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordUtility.class);

    private static final int SALT_LENGTH = 64;

    /**
     * Encrypt the provided password using base64-encoded salted SHA-512.
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
        byte[] salt = Hex.encodeToString(generateSalt()).getBytes();
        digest.update(salt);
        digest.update(plaintext.getBytes());
        return new String(salt) + Hex.encodeToString(digest.digest());
    }

    private byte[] generateSalt() {
        Random random = new SecureRandom();
        byte[] bytes = new byte[SALT_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

}
