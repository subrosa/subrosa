package com.subrosa.api.actions.list.serialization.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.subrosa.api.actions.list.Filter;

import java.io.IOException;
import java.util.List;

/**
 * Handles JSON serialization of filter lists.
 */
public class FilterListJsonSerializer extends JsonSerializer<List<Filter>> {

    @Override
    public void serialize(List<Filter> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (Filter filter : value) {
            jgen.writeStartObject();
            jgen.writeObjectField(filter.getFilterKey(), filter.getValue());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }

}
