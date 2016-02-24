package com.subrosagames.subrosa.test.matchers;

import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Matcher for paginated lists with a certain {@code results.size()}.
 */
public final class IsPaginatedListWithResultsSize extends IsPaginatedList {

    private final int size;

    private IsPaginatedListWithResultsSize(int size) {
        this.size = size;
    }

    /**
     * Factory for matching results size.
     *
     * @param size results size
     * @return paginated list matcher
     */
    @Factory
    public static Matcher<Map> hasResultsSize(int size) {
        return new IsPaginatedListWithResultsSize(size);
    }

    @Override
    protected boolean matchesSafely(Map map, Description description) {
        boolean matches = super.matchesSafely(map, description);
        if (!(map.get("results") instanceof List)) {
            description.appendText("results is not an array");
            matches = false;
        } else {
            List results = (List) map.get("results");
            if (results.size() != size) {
                description.appendText("results size is " + results.size());
                matches = false;
            }
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        super.describeTo(description);
        description.appendText("| results size " + size + " ");
    }

}
