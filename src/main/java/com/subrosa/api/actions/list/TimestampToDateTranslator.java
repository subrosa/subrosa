package com.subrosa.api.actions.list;


import java.util.Date;

/**
 * Translates a UNIX timestamp to a {@link java.util.Date} for use in filtering.
 */
public class TimestampToDateTranslator implements FilterValueTranslator<String, Date> {

    @Override
    public Date translate(String timestamp) {
        return new Date(Long.valueOf(timestamp));
    }

}
