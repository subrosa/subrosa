package com.subrosagames.subrosa.test.matchers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.junit.Test;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.NotificationListContents.NotificationDetailKey.withDetailKey;
import static com.subrosagames.subrosa.test.matchers.NotificationListContents.NotificationListHas.hasNotification;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;


/**
 */
public class NotificationListContentsTest {

    @Test
    public void testDetailKeyWithEmptyDetails() throws Exception {
        String s = "[ { \"details\": {} } ]";
        JSONArray input = (JSONArray) JSONValue.parse(s);
        assertThat(input, not(hasNotification(withDetailKey("anything"))));
    }

    @Test
    public void testMatchesDetailKey() throws Exception {
        String s = "[ { \"details\": {\"field1\": \"field1 is wrong\", \"field2\": \"field2 is wrong\"} } ]";
        JSONArray input = (JSONArray) JSONValue.parse(s);
        assertThat(input, hasNotification(withDetailKey("field1")));
        assertThat(input, hasNotification(withDetailKey("field2")));
    }

    @Test
    public void testDetailKeyWithWrongDetails() throws Exception {
        String s = "[ { \"details\": {\"field1\": \"field1 is wrong\", \"field2\": \"field2 is wrong\"} } ]";
        JSONArray input = (JSONArray) JSONValue.parse(s);
        assertThat(input, not(hasNotification(withDetailKey("field3"))));
    }
}
