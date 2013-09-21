package com.subrosagames.subrosa.test.matchers;

import org.junit.Test;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;


/**
 * Test {@link IsNotificationList}.
 */
public class IsNotificationListTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testMatches() throws Exception {
        String s = "{notifications:[{code:1010101,severity:\"ERROR\",text:\"it's all wrong\"}]}";
        JSONObject input = (JSONObject) JSONValue.parse(s);
        assertThat(input, is(notificationList()));
    }

    @Test
    public void testMatchesEmptyList() throws Exception {
        JSONObject input = (JSONObject) JSONValue.parse("{notifications:[]}");
        assertThat(input, is(notificationList()));
    }

    @Test
    public void testMissingNotifications() throws Exception {
        JSONObject input;
        input = (JSONObject) JSONValue.parse("{}");
        assertThat(input, not(notificationList()));
        input = (JSONObject) JSONValue.parse("{something: 3}");
        assertThat(input, not(notificationList()));
    }

    @Test
    public void testMissingNotificationContents() throws Exception {
        JSONObject input;
        input = (JSONObject) JSONValue.parse("{notifications:[{}]}");
        assertThat(input, not(notificationList()));
        input = (JSONObject) JSONValue.parse("{notifications:[{something: 3}]}");
        assertThat(input, not(notificationList()));
        input = (JSONObject) JSONValue.parse("{notifications:[{text:\"it's all wrong\"}]}");
        assertThat(input, not(notificationList()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
