package com.subrosa.api.actions.list.serialization.xml;

import com.subrosa.api.actions.list.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jgore
 * Date: 12/19/11
 * Time: 7:17 午後
 * To change this template use File | Settings | File Templates.
 */
public class FilterListXmlAdapter extends XmlAdapter<Object, List<Filter>> {

    private static final Logger LOG = LoggerFactory.getLogger(FilterListXmlAdapter.class);

    @Override
    public List<Filter> unmarshal(Object domTree) {
        List<Filter> filters = new ArrayList<Filter>();
        Element content = (Element) domTree;
        NodeList childNodes = content.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                String key = child.getNodeName();
                // Skip text nodes
                if (key.startsWith("#")) {
                    continue;
                }
                String value = ((Text) child.getChildNodes().item(0)).getWholeText();
                filters.add(new Filter(key, value));
            }
        }
        return filters;
    }

    @Override
    public Object marshal(List<Filter> filters) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element customXml = doc.createElement("Map");
            for (Filter filter : filters) {
                Element keyValuePair = doc.createElement(filter.getFilterKey());
                keyValuePair.appendChild(doc.createTextNode(filter.getValue().toString()));
                customXml.appendChild(keyValuePair);
            }
            return customXml;
        } catch (Exception e) { // SUPPRESS CHECKSTYLE IllegalCatch
            LOG.error("Exception occurrd when attempting to marshal filter list into XML!", e);
        }
        return null;
    }
}
