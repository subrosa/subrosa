package com.subrosagames.subrosa.test.matchers;

import org.hamcrest.core.Is;
import org.junit.Test;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Test {@link IsNotificationList}.
 */
public class IsNotificationListTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testMatches() throws Exception {
        String s = "{notifications:[{code:1010101,severity:\"ERROR\",text:\"it's all wrong\"}]}";
        JSONObject input = (JSONObject) JSONValue.parse(s);
        assertThat(input, Is.is(IsNotificationList.notificationList()));
    }

    @Test
    public void testMatchesEmptyList() throws Exception {
        JSONObject input = (JSONObject) JSONValue.parse("{notifications:[]}");
        assertThat(input, Is.is(IsNotificationList.notificationList()));
    }

    @Test
    public void testMissingNotifications() throws Exception {
        JSONObject input;
        input = (JSONObject) JSONValue.parse("{}");
        assertThat(input, not(IsNotificationList.notificationList()));
        input = (JSONObject) JSONValue.parse("{something: 3}");
        assertThat(input, not(IsNotificationList.notificationList()));
    }

    @Test
    public void testMissingNotificationContents() throws Exception {
        JSONObject input;
        input = (JSONObject) JSONValue.parse("{notifications:[{}]}");
        assertThat(input, not(IsNotificationList.notificationList()));
        input = (JSONObject) JSONValue.parse("{notifications:[{something: 3}]}");
        assertThat(input, not(IsNotificationList.notificationList()));
        input = (JSONObject) JSONValue.parse("{notifications:[{text:\"it's all wrong\"}]}");
        assertThat(input, not(IsNotificationList.notificationList()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
