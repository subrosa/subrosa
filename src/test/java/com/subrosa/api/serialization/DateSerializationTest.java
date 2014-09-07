package com.subrosa.api.serialization;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link com.subrosa.api.serialization.DateSerialization}.
 */
public class DateSerializationTest {

    /**
     * Date to test parsing against.
     */
    private static final Date TEST_DATE = new Date(872835240000L); // Judgement Day! (tm)
    private static final String TEST_DATE_UTC_STR = "1997-08-29T06:14:00.000Z";
    private static final String TEST_DATE_TZ_STR = "1997-08-29T02:14:00.000-04:00";

    /**
     * Test serialization of dates.
     */
    @Test
    public void testSerialize() {
        DateSerialization serializer = new DateSerialization();
        String isoDate = serializer.serialize(TEST_DATE);
        assertEquals(TEST_DATE_UTC_STR, isoDate);
    }

    /**
     * Test deserialization of dates in UTC.
     */
    @Test
    public void testUTCDeserialization() {
        DateSerialization deserializer = new DateSerialization();
        Date date = deserializer.deserialize(TEST_DATE_UTC_STR);

        assertTrue("UTC date was not correctly parsed", date.equals(TEST_DATE));
    }

    /**
     * Test deserialization of dates with timezone offset.
     */
    @Test
    public void testTZDeserialization() {
        DateSerialization deserializer = new DateSerialization();
        Date date = deserializer.deserialize(TEST_DATE_TZ_STR);

        assertTrue("Timezone offset date was not correctly parsed", date.equals(TEST_DATE));
    }
    
    /**
     * Test expected exception when parsing a bad date string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBadTimeString() {
        DateSerialization deserializer = new DateSerialization();
        deserializer.deserialize("FOUR O' CLOCK NEXT TUESDAY");
    }

}
