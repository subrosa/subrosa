package com.subrosagames.subrosa.test.matchers;

import org.junit.Test;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailField.withDetailField;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;


/**
 * Test {@link NotificationListHas}.
 */
public class NotificationListContentsTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testHasDetailFieldWithEmptyDetails() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getEmptyDetailsString());
        assertThat(input, not(hasNotification(withDetailField("anything"))));
    }

    @Test
    public void testDetailFieldWithWrongField() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getDetailFieldString("field1"));
        assertThat(input, not(hasNotification(withDetailField("field2"))));
    }

    @Test
    public void testHasDetailField() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getDetailFieldString("field1"));
        assertThat(input, hasNotification(withDetailField("field1")));
    }

    @Test
    public void testHasDetailWithEmptyDetails() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getEmptyDetailsString());
        assertThat(input, not(hasNotification(withDetail("rightField", "rightConstraint"))));
    }

    @Test
    public void testHasDetailWithWrongField() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getDetailsString("wrongField", "rightConstraint"));
        assertThat(input, not(hasNotification(withDetail("rightField", "rightConstraint"))));
    }

    @Test
    public void testHasDetailWithRightFieldWrongValue() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getDetailsString("rightField", "wrongConstraint"));
        assertThat(input, not(hasNotification(withDetail("rightField", "rightConstraint"))));
    }

    @Test
    public void testHasDetail() throws Exception {
        JSONArray input = (JSONArray) JSONValue.parse(getDetailsString("rightField", "rightConstraint"));
        assertThat(input, hasNotification(withDetail("rightField", "rightConstraint")));
    }

    // CHECKSTYLE-ON: JavadocMethod

    private String getEmptyDetailsString() {
        return "[ { \"details\": {} } ]";
    }

    private String getDetailFieldString(String field) {
        return "[ { \"details\": {\"field\": \"" + field + "\", \"constraint\": \"irrelevant\"} } ]";
    }

    private String getDetailsString(String field, String constraint) {
        return "[ { \"details\": {\"field\": \"" + field + "\", \"constraint\": \"" + constraint + "\"} } ]";
    }

}
