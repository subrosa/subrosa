package com.subrosa.api.actions.list;

import com.subrosa.api.actions.list.Sort;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link com.subrosa.api.actions.list.Sort}.
 */
public class SortTest {

    @Test // SUPPRESS CHECKSTYLE JavadocMethod
    public void testSortParsing() {
        Sort sort;

        sort = new Sort("name");
        Assert.assertEquals("name", sort.getField());
        Assert.assertTrue(sort.isAscending());
        Assert.assertEquals("name", sort.getSortField());

        sort = new Sort("-createDate");
        Assert.assertEquals("createDate", sort.getField());
        Assert.assertFalse(sort.isAscending());
        Assert.assertEquals("-createDate", sort.getSortField());

        sort = new Sort();
        sort.setSortField("-thisField");
        Assert.assertEquals("-thisField", sort.getSortField());

        sort.setField("newField");
        Assert.assertEquals("-newField", sort.getSortField());

        sort.setAscending(true);
        Assert.assertEquals("newField", sort.getSortField());
    }
}
