package com.subrosa.api.notification;

/**
 * Interface for response codes used in notifications. Provides a notification code and a default message.
 */
public interface Code {
    /**
     * Retrieves the integer code.
     * 
     * @return the integer code
     */
    String getCode();

    /**
     * Retrieves the default text message related to this code.
     * 
     * @return the default text message
     */
    String getDefaultMessage();
}
