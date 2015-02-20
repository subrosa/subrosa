package com.subrosagames.subrosa.api.notification;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a notification message related to a request.
 */
public class Notification {

    private Code code;
    private Severity severity;
    private String text;
    private Map<DetailKey, String> details;

    /**
     * Constructs an empty notification.
     */
    public Notification() {
    }

    /**
     * Constructs a Notification with the specified code, severity, and text.
     *
     * @param code     the notification code
     * @param severity the severity of the message (error/warn/info)
     * @param text     the text of the notification
     */
    public Notification(Code code, Severity severity, String text) {
        this.code = code;
        this.severity = severity;
        this.text = text;
    }

    /**
     * Constructs a Notification with the given code and severity.
     *
     * @param code     the notification code
     * @param severity the severity of the message (error/warn/info)
     */
    public Notification(Code code, Severity severity) {
        this.code = code;
        this.severity = severity;
        this.text = code.getDefaultMessage();
    }

    /**
     * Retrieves the notification code.
     *
     * @return the notification code
     */
    @JsonIgnore
    public Code getCode() {
        return code;
    }

    /**
     * Sets the notification code.
     *
     * @param code the notification code
     */
    public void setCode(Code code) {
        this.code = code;
    }

    /**
     * Retrieves the severity of the notification.
     *
     * @return the severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Sets the severity of the notification.
     *
     * @param severity the severity
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    /**
     * Retrieves the collection of details related to this notification.
     *
     * @return the collection of notification details
     */
    @JsonSerialize(
            include = JsonSerialize.Inclusion.NON_NULL
    )
    public Map<DetailKey, String> getDetails() {
        return details;
    }

    /**
     * Sets the collection of notification details for this notification.
     *
     * @param details the collection of notification details
     */
    public void setDetails(Map<DetailKey, String> details) {
        this.details = details;
    }

    /**
     * Retrieves the text for the notification.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text for the notification.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the integer notification code.
     *
     * @return the integer code
     */
    @JsonProperty("code")
    private String getStringCode() {
        return code.getCode();
    }

    /**
     * Enumeration of valid fields for the notification details map.
     */
    public enum DetailKey {
        /**
         * Used when specifying field name.
         */
        FIELD("field"),
        /**
         * Used when specifying constraint violated.
         */
        CONSTRAINT("constraint"),
        /**
         * Used when specifying a constraint value.
         */
        VALUE("value");

        private final String value;

        DetailKey(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
