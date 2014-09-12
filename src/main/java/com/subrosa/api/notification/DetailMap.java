package com.subrosa.api.notification;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Container class for data from a details map.
 */
@XmlRootElement(name = "details")
public class DetailMap {
    private List<DetailMapEntry> entryList = new ArrayList<DetailMapEntry>();

    @XmlElement(name = "detail")
    public List<DetailMapEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DetailMapEntry> entryList) {
        this.entryList = entryList;
    }
}
