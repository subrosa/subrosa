package com.subrosagames.subrosa.test.matchers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Matcher for paginated lists with a certain {@code results.size()}.
 */
public class IsPaginatedListWithResultsSize extends IsPaginatedList {

    private final int size;

    public IsPaginatedListWithResultsSize(int size) {
        this.size = size;
    }

    /**
     * Factory method.
     * @return instance
     */
    @Factory
    public static Matcher<JSONObject> hasResultsSize(int size) {
        return new IsPaginatedListWithResultsSize(size);
    }

    @Override
    protected boolean matchesSafely(JSONObject jsonObject, Description description) {
        boolean matches = super.matchesSafely(jsonObject, description);
        if (!(jsonObject.get("results") instanceof JSONArray)) {
            description.appendText("results is not an array");
            matches = false;
        } else {
            JSONArray results = (JSONArray) jsonObject.get("results");
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
