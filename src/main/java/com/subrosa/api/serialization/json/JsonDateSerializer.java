package com.subrosa.api.serialization.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.subrosa.api.serialization.DateSerialization;

/**
 * Serializes Date objects to JSON using ISO8601 format in UTC.
 */
public class JsonDateSerializer extends JsonSerializer<Date> {

    private static final DateSerialization DATE_FORMATTER = new DateSerialization();

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }

    /**
     * Serializes Date objects to JSON using ISO8601 format.
     *
     * @param date      the date
     * @param generator the json generator
     * @param provider  the serializer provider
     * @throws java.io.IOException when an IO error occurs
     */
    @Override
    public void serialize(Date date, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(DATE_FORMATTER.serialize(date));
    }

}
