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

    private final AbstractNotificationMatcher matcher; // SUPPRESS CHECKSTYLE IllegalType

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
    public static Matcher<JSONArray> hasNotification(AbstractNotificationMatcher matcher) { // SUPPRESS CHECKSTYLE IllegalType
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
     * Abstract parent for notification matchers.
     */
    public abstract static class AbstractNotificationMatcher extends TypeSafeDiagnosingMatcher<JSONObject> { }

    /**
     * Matches notification with an error code.
     */
    public static class NotificationWithCodeMatcher extends AbstractNotificationMatcher { // SUPPRESS CHECKSTYLE FinalClassCheck

        private String code;

        private NotificationWithCodeMatcher(String code) {
            this.code = code;
        }

        /**
         * Factory for matching notification with an error code.
         *
         * @param code error code
         * @return notification with constraint matcher
         */
        @Factory
        public static NotificationWithCodeMatcher withCode(String code) {
            return new NotificationWithCodeMatcher(code);
        }

        @Override
        protected boolean matchesSafely(JSONObject jsonObject, Description description) {
            boolean matches = true;
            if (!jsonObject.containsKey("code")) {
                description.appendText("| does not contain code ");
                matches = false;
            } else {
                String errorCode = (String) jsonObject.get("code");
                if (!code.equals(errorCode)) {
                    description.appendText("| does not have code " + code + " (found " + errorCode + ") ");
                    matches = false;
                }
            }
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("error code " + code);
        }
    }

    /**
     * Matches notification with a details map.
     */
    public static class NotificationWithDetailsMatcher extends AbstractNotificationMatcher { // SUPPRESS CHECKSTYLE FinalClassCheck

        private NotificationWithDetailsMatcher() {
        }

        /**
         * Factory for matching notification with constraint details.
         *
         * @return notification with constraint matcher
         */
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
    public static class NotificationDetailField extends NotificationWithDetailsMatcher { // SUPPRESS CHECKSTYLE FinalClassCheck

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

    /**
     * Matcher for a notification containing specific constraint details.
     */
    public static class NotificationDetail extends NotificationDetailField { // SUPPRESS CHECKSTYLE FinalClassCheck

        private final String field;
        private final String constraint;
        private final Object value;

        private NotificationDetail(String field, String constraint, Object value) {
            super(field);
            this.field = field;
            this.constraint = constraint;
            this.value = value;
        }

        /**
         * Factory for matching notification with a detail and constraint.
         *
         * @param field      field
         * @param constraint constraint
         * @return notification with detail matcher
         */
        @Factory
        public static NotificationWithDetailsMatcher withDetail(String field, String constraint) {
            return new NotificationDetail(field, constraint, null);
        }

        /**
         * Factory for matching notification with a detail and constraint with a constraint value.
         *
         * @param field      field
         * @param constraint constraint
         * @param value      value
         * @return notification with detail matcher
         */
        @Factory
        public static NotificationWithDetailsMatcher withDetail(String field, String constraint, Object value) {
            return new NotificationDetail(field, constraint, value);
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
                if (value != null && !value.equals(details.get("value"))) {
                    description.appendText("| field " + field + " does not have constraint value " + value + " (found " + details.get("value") + ") ");
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
