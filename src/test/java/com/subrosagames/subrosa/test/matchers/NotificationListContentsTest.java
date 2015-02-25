package com.subrosagames.subrosa.test.matchers;

import org.junit.Test;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailField.withDetailField;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationWithCodeMatcher.withCode;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;


/**
 * Test {@link NotificationListHas}.
 */
public class NotificationListContentsTest {

    // CHECKSTYLE-OFF: JavadocMethod


    @Test
    public void testHasErrorCode() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withErrorCode("correct"));
        assertThat(input, hasNotification(withCode("correct")));
    }

    @Test
    public void testHasWrongErrorCode() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withErrorCode("wrong"));
        assertThat(input, not(hasNotification(withCode("correct"))));
    }

    @Test
    public void testHasDetailFieldWithEmptyDetails() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withEmptyDetailsString());
        assertThat(input, not(hasNotification(withDetailField("anything"))));
    }

    @Test
    public void testDetailFieldWithWrongField() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withDetailFieldString("field1"));
        assertThat(input, not(hasNotification(withDetailField("field2"))));
    }

    @Test
    public void testHasDetailField() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withDetailFieldString("field1"));
        assertThat(input, hasNotification(withDetailField("field1")));
    }

    @Test
    public void testHasDetailWithEmptyDetails() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withEmptyDetailsString());
        assertThat(input, not(hasNotification(withDetail("rightField", "rightConstraint"))));
    }

    @Test
    public void testHasDetailWithWrongField() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withDetailsString("wrongField", "rightConstraint"));
        assertThat(input, not(hasNotification(withDetail("rightField", "rightConstraint"))));
    }

    @Test
    public void testHasDetailWithRightFieldWrongValue() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withDetailsString("rightField", "wrongConstraint"));
        assertThat(input, not(hasNotification(withDetail("rightField", "rightConstraint"))));
    }

    @Test
    public void testHasDetail() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(withDetailsString("rightField", "rightConstraint"));
        assertThat(input, hasNotification(withDetail("rightField", "rightConstraint")));
    }

    // CHECKSTYLE-ON: JavadocMethod

    private String withEmptyDetailsString() {
        return "[ { \"details\": {} } ]";
    }

    private String withDetailFieldString(String field) {
        return "[ { \"details\": {\"field\": \"" + field + "\", \"constraint\": \"irrelevant\"} } ]";
    }

    private String withDetailsString(String field, String constraint) {
        return "[ { \"details\": {\"field\": \"" + field + "\", \"constraint\": \"" + constraint + "\"} } ]";
    }

    private String withErrorCode(String code) {
        return "[ { \"code\": \"" + code + "\" } ]";
    }

}
