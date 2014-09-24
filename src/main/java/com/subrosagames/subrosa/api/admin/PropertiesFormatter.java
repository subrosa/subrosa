package com.subrosagames.subrosa.api.admin;

import java.util.Map;
import java.util.Properties;

/**
 * Formats {@link Properties} into a string for consumption by a user.
 */
public class PropertiesFormatter {

    private final Properties properties;

    /**
     * Construct with properties.
     *
     * @param properties properties
     */
    public PropertiesFormatter(Properties properties) {
        this.properties = properties;
    }

    /**
     * Constructs a string representation of the {@link Properties}.
     *
     * @return properties as a string
     */
    public String getAllAsString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            builder.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        return builder.toString();
    }
}
