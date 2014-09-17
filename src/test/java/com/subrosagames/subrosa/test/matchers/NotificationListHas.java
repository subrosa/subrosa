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

    private final NotificationWithDetailsMatcher matcher;

    private NotificationListHas(NotificationWithDetailsMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * Factory for matching existence of at least one matching notification.
     *
     * @param matcher notification matcher
     * @return notification list matcher
     */
    @Factory
    public static Matcher<JSONArray> hasNotification(NotificationWithDetailsMatcher matcher) {
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

    /**
     * Matches notification with a details map.
     */
    public static class NotificationWithDetailsMatcher extends TypeSafeDiagnosingMatcher<JSONObject> {

        private NotificationWithDetailsMatcher() {  }

        @Factory
        public static NotificationWithDetailsMatcher withDetails() {
            return new NotificationWithDetailsMatcher();
        }

        @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = true;
            if (!jsonObject.containsKey("details")) {
                description.appendText("| does not contain details ");
                matches = false;
            } else {
                JSONObject details = (JSONObject) jsonObject.get("details");
                if (!details.containsKey("field") || !details.containsKey("constraint")) {
                    description.appendText("| details entry is missing field or constraint ");
                    matches = false;
                }
            }
            return matches;
        }


        @Override
        public void describeTo(Description description) {
            description.appendText("with details");
        }
    }

    /**
     * Matches notification with a details map containing a specified field.
     */
    public static class NotificationDetailField extends NotificationWithDetailsMatcher {

        private final String field;

        private NotificationDetailField(String field) {
            this.field = field;
        }

        /**
         * Factory for matching notification with specified detail field.
         *
         * @param detailKey field
         * @return notification matcher
         */
        @Factory
        public static NotificationWithDetailsMatcher withDetailField(String detailKey) {
            return new NotificationDetailField(detailKey);
        }

        @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = super.matchesSafely(jsonObject, description);
            if (matches) {
                JSONObject details = (JSONObject) jsonObject.get("details");
                if (!field.equals(details.get("field"))) {
                    description.appendText("| does not have field " + field + " ");
                    matches = false;
                }
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("detail field " + field);
        }
    }

    public static class NotificationDetail extends NotificationDetailField {

        private final String field;
        private final String constraint;

        private NotificationDetail(String field, String constraint) {
            super(field);
            this.field = field;
            this.constraint = constraint;
        }

        /**
         * Factory for matching notification with a detail and constraint.
         *
         * @param detailKey field
         * @param detailValue constraint
         * @return
         */
        @Factory
        public static NotificationWithDetailsMatcher withDetail(String detailKey, String detailValue) {
            return new NotificationDetail(detailKey, detailValue);
        }

         @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = super.matchesSafely(jsonObject, description);
            if (matches) {
                JSONObject details = (JSONObject) jsonObject.get("details");
                if (!constraint.equals(details.get("constraint"))) {
                    description.appendText("| field " + field + " does not have constraint " + constraint + " ");
                    matches = false;
                }
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("detail (" + field + " => " + constraint + ")");
        }
    }
}
