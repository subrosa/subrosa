package com.subrosagames.subrosa.util;

/**
 * Helpers for working with objects.
 */
public final class ObjectUtils {

    private ObjectUtils() { }

    /**
     * Returns the given object or, if it is {@code null}, the provided default.
     * @param object value
     * @param defaultValue default
     * @param <T> value type
     * @return {@code value} or, if {@code null}, {@code defaultValue}
     */
    public static <T> T defaultIfNull(T object, T defaultValue) {
        if (object != null) {
            return object;
        }
        return defaultValue;
    }
}
