package com.subrosagames.subrosa.test.matchers;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;


/**
 * Matcher for paginated lists with a certain {@code resultCount}.
 */
public final class IsPaginatedListWithResultCount extends IsPaginatedList {

    private final int count;

    private IsPaginatedListWithResultCount(int count) {
        this.count = count;
    }

    /**
     * Factory for matching total result count.
     *
     * @param count result count
     * @return paginated list matcher
     */
    @Factory
    public static Matcher<Map> hasResultCount(int count) {
        return new IsPaginatedListWithResultCount(count);
    }

    @Override
    protected boolean matchesSafely(Map map, Description description) {
        boolean matches = super.matchesSafely(map, description);
        if (!map.get("resultCount").equals(count)) {
            description.appendText("result count is " + map.get("resultCount"));
            matches = false;
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("| paginated list with total results " + count + " ");
    }
}
