package com.subrosa.api.serialization.xml;

import com.subrosa.api.serialization.DateSerialization;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * JAXB xml adapter that uses subrosa's standard date formatting for marshalling and unmarshalling.
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    private static final DateSerialization DATE_FORMATTER = new DateSerialization();

    @Override
    public String marshal(Date date) throws Exception {
        return DATE_FORMATTER.serialize(date);
    }

    @Override
    public Date unmarshal(String dateStr) throws Exception {
        return DATE_FORMATTER.deserialize(dateStr);
    }

}
