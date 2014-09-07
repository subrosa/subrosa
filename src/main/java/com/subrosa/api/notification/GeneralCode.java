package com.subrosa.api.notification;

/**
 * Enumeration of general purpose API notification codes. All int values should start with 10.
 */
public enum GeneralCode implements Code {
    // CHECKSTYLE-OFF: JavadocVariable
    NOT_FOUND                   ("notFound", "Not found"),
    FORBIDDEN                   ("forbidden", "Forbidden"),
    NOT_ACCEPTABLE              ("notAcceptable", "Media type in Accept header not supported"),
    UNAUTHORIZED_FIELD_ACCESS   ("unauthorizedFieldAccess", "Unauthorized field access"),
    MISSING_REQUIRED_FIELD      ("missingRequiredField", "Missing required field"),
    INVALID_FIELD_VALUE         ("invalidValue", "Invalid value for field"),
    READ_ONLY_FIELD             ("readOnly", "Cannot set read-only field"),
    INVALID_REQUEST_ENTITY      ("invalidRequestEntity", "The request body is missing or of the wrong type"),
    INTERNAL_ERROR              ("internalError", "Internal error"),
    DOMAIN_OBJECT_NOT_FOUND     ("objectNotFound", "Domain object not found"),
    DESERIALIZATION_ERROR       ("requestCorrupt", "Error deserializing request"),
    FILE_CORRUPT                ("fileCorrupt", "File is corrupt"),
    FILE_TOO_LARGE              ("fileTooLarge", "File is too large"),
    FILE_NOT_FOUND              ("fileNotFound", "File not found");
    // CHECKSTYLE-ON: JavadocVariable

    private String code;
    private String defaultMessage;

    /**
     * Constructs a GeneralNotificationCode.
     *
     * @param code the integer code
     * @param defaultMessage the default text message related to this code
     */
    GeneralCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

}
