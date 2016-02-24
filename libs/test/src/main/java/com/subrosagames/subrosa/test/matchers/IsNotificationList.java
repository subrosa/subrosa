package com.subrosagames.subrosa.test.matchers;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import net.minidev.json.JSONArray;

import static org.hamcrest.CoreMatchers.everyItem;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.IsNotification.isNotification;


/**
 * Matches a JSON object representing the API's paginated list.
 */
public class IsNotificationList extends TypeSafeDiagnosingMatcher<Map> {

    /**
     * Factory for matching notification list.
     *
     * @return notification list matcher
     */
    @Factory
    public static Matcher<Map> notificationList() {
        return new IsNotificationList();
    }

    @Override
    protected boolean matchesSafely(Map map, Description description) {
        boolean matches = true;
        if (!map.containsKey("notifications")) {
            description.appendText("| notifications wrapper missing ");
            matches = false;
        } else {
            JSONArray notifications = (JSONArray) map.get("notifications");
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
        description.appendText("notification list");
    }

    /**
     * Matcher for a notification.
     */
    static class IsNotification extends TypeSafeDiagnosingMatcher<Map> {

        @Factory
        public static IsNotification isNotification() {
            return new IsNotification();
        }

        @Override
        protected boolean matchesSafely(Map map, Description description) {
            boolean matches = true;
            if (!map.containsKey("code")) {
                description.appendText("| code missing ");
                matches = false;
            }
            if (!map.containsKey("severity")) {
                description.appendText("| severity missing ");
                matches = false;
            }
            if (!map.containsKey("text")) {
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
