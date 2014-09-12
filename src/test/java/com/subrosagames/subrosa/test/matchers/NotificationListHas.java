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
     *
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
     * Matches notification with a details map containing a specified field.
     */
    public static final class NotificationDetailField extends AbstractNotificationMatcher {

        private final String detailField;

        private NotificationDetailField(String detailField) {
            this.detailField = detailField;
        }

        /**
         * Factory for matching notification with specified detail field.
         *
         * @param detailKey detail field
         * @return notification matcher
         */
        @Factory
        public static AbstractNotificationMatcher withDetailField(String detailKey) {
            return new NotificationDetailField(detailKey);
        }

        @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = true;
            if (!jsonObject.containsKey("details")) {
                description.appendText("| does not contain details ");
                matches = false;
            } else {
                JSONObject details = (JSONObject) jsonObject.get("details");
                if (!details.containsKey("field") || !details.get("field").equals(detailField)) {
                    description.appendText("| does not contain detail field " + detailField + " ");
                    matches = false;
                }
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("detail field " + detailField);
        }
    }
}
