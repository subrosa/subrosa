package com.subrosagames.subrosa.api.web;

import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link ApiGamePlayerController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/games.xml")
public class ApiGamePlayerControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testJoinUnauthenticatedUnauthorized() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "fun_times"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testJoinGameWithoutName() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "fun_times")
                        .with(user("player1@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))));
    }

    @Test
    public void testJoinGameWithUserTooYoung() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "must_be_18")
                        .with(user("young@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("age", "atLeast", "18"))));
    }

    @Test
    public void testJoinGameMissingRequiredPlayerInfo() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "last_wish_required")
                        .with(user("player1@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("lastWish", "required"))));
    }

    @Test
    public void testJoinGame() throws Exception {
        String lastWish = "You don't have to do this. You can still let me win.";
        String name = "The Killer Rabbit";
        mockMvc.perform(
                post("/game/{url}/player", "fun_times")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("name", name)
                                .addChild("attributes",
                                        jsonBuilder()
                                                .add("Last Wish", lastWish))
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.attributes.Last Wish").value(lastWish));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
