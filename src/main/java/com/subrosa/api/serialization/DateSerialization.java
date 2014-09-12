package com.subrosa.api.serialization;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Class for handling standard serialization/deserialization of dates.
 */
public class DateSerialization {

    private static final DateTimeFormatter DATE_PARSER = ISODateTimeFormat.dateTime();
    private static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    /**
     * Converts a date to a an ISO8601 string in UTC.
     *
     * @param date the date to serialize
     * @return ISO8601 UTC date string
     */
    public String serialize(Date date) {
        String dateStr = DATE_FORMATTER.print(date.getTime());
        return dateStr;
    }

    /**
     * Converts an ISO8601 date string to a {@link java.util.Date}.
     *
     * @param dateStr an ISO8601 date string
     * @return date corresponding to the string
     * @throws IllegalArgumentException if the date string is invalid
     */
    public Date deserialize(String dateStr) {
        DateTime jodaDate = DATE_PARSER.parseDateTime(dateStr);
        return jodaDate.toDate();
    }

}
