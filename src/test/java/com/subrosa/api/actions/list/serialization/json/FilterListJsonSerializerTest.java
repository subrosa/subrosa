package com.subrosa.api.actions.list.serialization.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.subrosa.api.actions.list.Filter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test {@link com.subrosa.api.actions.list.serialization.json.FilterListJsonSerializer}.
 */
public class FilterListJsonSerializerTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocType

    @Test
    public void testSerialize() throws IOException {
        List<Filter> filters = new ArrayList<Filter>();
        String json;

        filters.add(new Filter("numberGreaterThan", 35));
        json = new ObjectMapper().writeValueAsString(new FilterContainer(filters));
        Assert.assertEquals("{\"filters\":[{\"numberGreaterThan\":35}]}", json);

        filters.add(new Filter("yourMomIsFat", true));
        json = new ObjectMapper().writeValueAsString(new FilterContainer(filters));
        Assert.assertEquals("{\"filters\":[{\"numberGreaterThan\":35},{\"yourMomIsFat\":true}]}", json);
    }

    public static class FilterContainer {

        private List<Filter> filters;

        public FilterContainer() { }

        public FilterContainer(List<Filter> filters) {
            this.filters = filters;
        }

        @JsonSerialize(using = FilterListJsonSerializer.class)
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
