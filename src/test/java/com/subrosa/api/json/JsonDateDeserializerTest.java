package com.subrosa.api.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.subrosa.api.serialization.json.JsonDateDeserializer;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link com.subrosa.api.serialization.json.JsonDateDeserializer}.
 */
public class JsonDateDeserializerTest {

    /**
     * Test that a bad date format results in a {@link JsonParseException}.
     */
    @Test(expected = JsonParseException.class)
    public void testBadDate() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);
        
        when(parser.getText()).thenReturn("LUNCHTIME LAST WEDNESDAY");
        
        JsonDateDeserializer deserializer = new JsonDateDeserializer();
        deserializer.deserialize(parser, context);
    }
    
}
