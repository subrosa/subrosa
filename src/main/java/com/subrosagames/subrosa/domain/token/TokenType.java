package com.subrosagames.subrosa.domain.token;

/**
 * Enumeration of types of generated tokens the system relies upon.
 */
public enum TokenType {

    /**
     * Token used in email address validation.
     */
    EMAIL_VALIDATION,
    /**
     * Token identifying a particular device.
     */
    DEVICE_AUTH
}
