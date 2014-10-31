package com.subrosagames.subrosa.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import net.minidev.json.JSONObject;

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
    public static Matcher<JSONObject> hasResultCount(int count) {
        return new IsPaginatedListWithResultCount(count);
    }

    @Override
    protected boolean matchesSafely(JSONObject jsonObject, Description description) {
        boolean matches = super.matchesSafely(jsonObject, description);
        if (!jsonObject.get("resultCount").equals(count)) {
            description.appendText("result count is " + jsonObject.get("resultCount"));
            matches = false;
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("| paginated list with total results " + count + " ");
    }
}
