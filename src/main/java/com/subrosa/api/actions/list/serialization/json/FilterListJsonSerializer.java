package com.subrosa.api.actions.list.serialization.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.subrosa.api.actions.list.Filter;

/**
 * Handles JSON serialization of @{link List}s of @{link Filter}s.
 */
public class FilterListJsonSerializer extends JsonSerializer<List<Filter>> {

    @Override
    public void serialize(List<Filter> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        for (Filter filter : value) {
            jgen.writeStartObject();
            jgen.writeObjectField(filter.getFilterKey(), filter.getValue());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }

}
