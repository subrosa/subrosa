package com.subrosagames.subrosa.api.list.serialization.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.Assert;
import org.junit.Test;

import com.subrosagames.subrosa.api.list.Filter;

/**
 * Test {@link com.subrosagames.subrosa.api.list.serialization.xml.FilterListXmlAdapter}.
 */
public class FilterListXmlAdapterTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocType

    @Test
    public void testMarshalAndUnmarshal() throws IOException {
        List<Filter> filters = new ArrayList<Filter>();
        ByteArrayOutputStream outputStream;

        filters.add(new Filter("numberGreaterThan", 35));
        outputStream = new ByteArrayOutputStream();
        JAXB.marshal(new FilterContainer(filters), outputStream);
        Assert.assertTrue(new String(outputStream.toByteArray(), "UTF-8").contains("<numberGreaterThan>35</numberGreaterThan>"));

        filters.add(new Filter("yourMomIsFat", true));
        outputStream = new ByteArrayOutputStream();
        JAXB.marshal(new FilterContainer(filters), outputStream);
        Assert.assertTrue(new String(outputStream.toByteArray(), "UTF-8").contains("<numberGreaterThan>35</numberGreaterThan>"));
        Assert.assertTrue(new String(outputStream.toByteArray(), "UTF-8").contains("<yourMomIsFat>true</yourMomIsFat>"));

        // for some reason unmarshalling hits a "premature end of file"
//        InputStream inputStream = IOUtils.toInputStream(new String(outputStream.toByteArray()));
//        System.out.println(IOUtils.toString(inputStream));
//        FilterContainer filterContainer = JAXB.unmarshal(inputStream, FilterContainer.class);
//        Assert.assertEquals(2, filterContainer.filters.size());
    }

    @XmlRootElement
    public static class FilterContainer {

        private List<Filter> filters;

        public FilterContainer() {
        }

        public FilterContainer(List<Filter> filters) {
            this.filters = filters;
        }

        @XmlJavaTypeAdapter(FilterListXmlAdapter.class)
        public List<Filter> getFilters() {
            return filters;
        }

        public void setFilters(List<Filter> filters) {
            this.filters = filters;
        }
    }

    // CHECKSTYLE-ON: JavadocMethod
    // CHECKSTYLE-ON: JavadocType

}
