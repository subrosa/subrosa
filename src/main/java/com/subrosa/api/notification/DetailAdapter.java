package com.subrosa.api.notification;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Adapter class that translates map entries into a class which can be serialized by JAXB.
 */
public class DetailAdapter extends XmlAdapter<DetailMap, Map<String, String>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DetailMap marshal(Map<String, String> arg0) throws Exception {
        DetailMap list = new DetailMap();
        if (arg0 != null) {
            for (Map.Entry<String, String> entry : arg0.entrySet()) {
                list.getEntryList().add(new DetailMapEntry(entry));
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> unmarshal(DetailMap arg0) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        if (arg0 != null) {
            for (DetailMapEntry entry : arg0.getEntryList()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }
}
