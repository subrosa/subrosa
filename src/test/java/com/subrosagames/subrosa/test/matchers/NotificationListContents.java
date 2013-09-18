package com.subrosagames.subrosa.test.matchers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.IsNotification.isNotification;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;


/**
 * Matches a JSON object representing the API's paginated list.
 */
public class NotificationListContents {

    public static class NotificationListHas extends TypeSafeDiagnosingMatcher<JSONArray> {

        private final NotificationMatcher matcher;

        public NotificationListHas(NotificationMatcher matcher) {
            this.matcher = matcher;
        }

        @Factory
        public static Matcher<JSONArray> hasNotification(NotificationMatcher matcher) {
            return new NotificationListHas(matcher);
        }

        @Override
        protected boolean matchesSafely(JSONArray jsonArray, Description description) {
            boolean matches = true;
            if (!hasItem(matcher).matches(jsonArray)) {
                hasItem(matcher).describeMismatch(jsonArray, description);
                matches = false;
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("notification list has ");
            description.appendDescriptionOf(matcher);
        }
    }

    static abstract class NotificationMatcher extends TypeSafeDiagnosingMatcher<JSONObject> {
    }

    public static class NotificationDetailKey extends NotificationMatcher {

        private final String detailKey;

        public NotificationDetailKey(String detailKey) {
            this.detailKey = detailKey;
        }

        @Factory
        public static NotificationMatcher withDetailKey(String detailKey) {
            return new NotificationDetailKey(detailKey);
        }

        @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = true;
            if (!jsonObject.containsKey("details")) {
                description.appendText("| does not contain details ");
                matches = false;
            } else {
                JSONObject details = (JSONObject) jsonObject.get("details");
                if (!details.containsKey(detailKey)) {
                    description.appendText("| does not contain detail key " + detailKey + " ");
                    matches = false;
                }
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("detail key " + detailKey);
        }
    }
}
