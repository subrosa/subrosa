package com.subrosagames.subrosa.test.matchers;

import net.minidev.json.JSONObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Matches a JSON object representing the API's paginated list.
 */
public class IsPaginatedList extends BaseMatcher<Object> {

    /**
     * Factory method.
     * @return instance
     */
    @Factory
    public static Matcher<Object> paginatedList() {
        return new IsPaginatedList();
    }

    @Override
    public boolean matches(Object o) {
        return o instanceof JSONObject && jsonMatches((JSONObject) o);
    }

    private boolean jsonMatches(JSONObject jsonObject) {
        return jsonObject.containsKey("limit")
                && jsonObject.containsKey("offset")
                && jsonObject.containsKey("results")
                && jsonObject.containsKey("resultCount");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("paginated list");
    }
}
