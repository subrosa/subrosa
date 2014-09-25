package com.subrosa.api.serialization.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.subrosa.api.serialization.DateSerialization;

/**
 * Deserializes Date objects from JSON using ISO8601 format.
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {

    private static final DateSerialization DATE_FORMATTER = new DateSerialization();

    /**
     * Parses a date from the ISO8601 format.
     *
     * @param parser  the json parser
     * @param context the deserialization context
     * @return a date corresponding to the parsed string
     * @throws java.io.IOException when an IO error occurs
     */
    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            String dateText = parser.getText();
            return DATE_FORMATTER.deserialize(dateText);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid date format.", parser.getCurrentLocation(), e);
        }
    }

}
