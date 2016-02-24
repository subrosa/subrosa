package com.subrosagames.subrosa.test.matchers;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matches a JSON object representing the API's paginated list.
 */
public class IsPaginatedList extends TypeSafeDiagnosingMatcher<Map> {

    /**
     * Factory for matching paginated list.
     *
     * @return paginated list matcher
     */
    @Factory
    public static Matcher<Map> paginatedList() {
        return new IsPaginatedList();
    }

    @Override
    protected boolean matchesSafely(Map map, Description description) {
        boolean matches = true;
        if (!map.containsKey("limit")) {
            description.appendText("limit missing");
            matches = false;
        }
        if (!map.containsKey("offset")) {
            description.appendText("offset missing");
            matches = false;
        }
        if (!map.containsKey("results")) {
            description.appendText("results missing");
            matches = false;
        }
        if (!map.containsKey("resultCount")) {
            description.appendText("resultCount missing");
            matches = false;
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("paginated list ");
    }

}
