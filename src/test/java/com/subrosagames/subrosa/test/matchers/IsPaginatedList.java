package com.subrosagames.subrosa.test.matchers;

import net.minidev.json.JSONObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matches a JSON object representing the API's paginated list.
 */
public class IsPaginatedList extends TypeSafeDiagnosingMatcher<JSONObject> {

    /**
     * Factory method.
     * @return instance
     */
    @Factory
    public static Matcher<JSONObject> paginatedList() {
        return new IsPaginatedList();
    }

    @Override
    protected boolean matchesSafely(JSONObject jsonObject, Description description) {
        boolean matches = true;
        if (!jsonObject.containsKey("limit")) {
            description.appendText("limit missing");
            matches = false;
        }
        if (!jsonObject.containsKey("offset")) {
            description.appendText("offset missing");
            matches = false;
        }
        if (!jsonObject.containsKey("results")) {
            description.appendText("results missing");
            matches = false;
        }
        if (!jsonObject.containsKey("resultCount")) {
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
