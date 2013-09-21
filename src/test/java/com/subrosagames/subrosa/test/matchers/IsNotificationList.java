package com.subrosagames.subrosa.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.hamcrest.Matchers.everyItem;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.IsNotification.isNotification;


/**
 * Matches a JSON object representing the API's paginated list.
 */
public class IsNotificationList extends TypeSafeDiagnosingMatcher<JSONObject> {

    /**
     * Factory for matching notification list.
     * @return notification list matcher
     */
    @Factory
    public static Matcher<JSONObject> notificationList() {
        return new IsNotificationList();
    }

    @Override
    protected boolean matchesSafely(JSONObject jsonObject, Description description) {
        boolean matches = true;
        if (!jsonObject.containsKey("notifications")) {
            description.appendText("| notifications wrapper missing ");
            matches = false;
        } else {
            JSONArray notifications = (JSONArray) jsonObject.get("notifications");
            if (!everyItem(isNotification()).matches(notifications)) {
                everyItem(isNotification()).describeMismatch(notifications, description);
                description.appendText("| child is not notification ");
                matches = false;
            }
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("paginated list");
    }

    static class IsNotification extends TypeSafeDiagnosingMatcher<JSONObject> {

        @Factory
        public static IsNotification isNotification() {
            return new IsNotification();
        }

        @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = true;
            if (!jsonObject.containsKey("code")) {
                description.appendText("| code missing ");
                matches = false;
            }
            if (!jsonObject.containsKey("severity")) {
                description.appendText("| severity missing ");
                matches = false;
            }
            if (!jsonObject.containsKey("text")) {
                description.appendText("| text missing ");
                matches = false;
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("notification");
        }
    }
}
