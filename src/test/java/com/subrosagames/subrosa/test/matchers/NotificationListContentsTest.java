package com.subrosagames.subrosa.test.matchers;

import org.junit.Test;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailField.withDetailField;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;


/**
 * Test {@link NotificationListHas}.
 */
public class NotificationListContentsTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testDetailFieldWithEmptyDetails() throws Exception {
        String s = "[ { \"details\": {} } ]";
        JSONArray input = (JSONArray) JSONValue.parse(s);
        assertThat(input, not(hasNotification(withDetailField("anything"))));
    }

    @Test
    public void testMatchesDetailField() throws Exception {
        String s = "[ { \"details\": {\"field\": \"field1\", \"constraint\": \"field1 is wrong\"} } ]";
        JSONArray input = (JSONArray) JSONValue.parse(s);
        assertThat(input, hasNotification(withDetailField("field1")));
    }

    @Test
    public void testDetailFieldWithWrongDetails() throws Exception {
        String s = "[ { \"details\": {\"field\": \"field1\", \"constraint\": \"field1 is wrong\"} } ]";
        JSONArray input = (JSONArray) JSONValue.parse(s);
        assertThat(input, not(hasNotification(withDetailField("field2"))));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
