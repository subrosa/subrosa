package com.subrosagames.subrosa.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.hamcrest.core.IsCollectionContaining.hasItem;


/**
 * Used for matching the contents of notification lists.
 */
public final class NotificationListHas extends TypeSafeDiagnosingMatcher<JSONArray> {

    private final AbstractNotificationMatcher matcher;

    private NotificationListHas(AbstractNotificationMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * Factory for matching existence of at least one matching notification.
     * @param matcher notification matcher
     * @return notification list matcher
     */
    @Factory
    public static Matcher<JSONArray> hasNotification(AbstractNotificationMatcher matcher) {
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

    private abstract static class AbstractNotificationMatcher extends TypeSafeDiagnosingMatcher<JSONObject> {
    }

    /**
     * Matches notification with a details map containing a specified key.
     */
    public static final class NotificationDetailKey extends AbstractNotificationMatcher {

        private final String detailKey;

        private NotificationDetailKey(String detailKey) {
            this.detailKey = detailKey;
        }

        /**
         * Factory for matching notification with specified detail key.
         * @param detailKey detail key
         * @return notification matcher
         */
        @Factory
        public static AbstractNotificationMatcher withDetailKey(String detailKey) {
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
