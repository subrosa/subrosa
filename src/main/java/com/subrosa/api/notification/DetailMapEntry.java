package com.subrosa.api.notification;

import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


/**
 * Class to help with serialization of Map Entries on notifications.
 */
public class DetailMapEntry {

    private String key;
    private String value;

    /**
     * Creates a new detail.
     */
    public DetailMapEntry() {
    }

    /**
     * Creates a detail based on the key-value pair in a map entry.
     *
     * @param entry map entry containing the key value pair represented by this detail
     */
    public DetailMapEntry(Entry<String, String> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    @XmlAttribute
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the string representation of the value object.
     *
     * @return the string representation of the detail's value.
     */
    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
