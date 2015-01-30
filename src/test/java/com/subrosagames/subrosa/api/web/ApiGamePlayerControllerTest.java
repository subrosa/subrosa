package com.subrosagames.subrosa.api.web;

import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link ApiGamePlayerController}.
 */
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
                post("/game/{url}/player", "last_wish_required")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("name", name)
                                .addChild("attributes",
                                        jsonBuilder()
                                                .add("lastWish", lastWish))
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.attributes.lastWish").value(lastWish));
    }

    @Test
    public void testJoinGameMissingPlayerName() throws Exception {
        performJoinWithImageAndAddress(null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))));
    }

    @Test
    public void testJoinGameMissingRequiredImageAndAddress() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "needs_image_and_address")
                        .with(user("player1@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("img", "required"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("addy", "required"))));
    }

    @Test
    public void testJoinGameWithImageAndAddress() throws Exception {
        String name = "PlayerKiller";
        performJoinWithImageAndAddress(name)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.attributes.img").exists())
                .andExpect(jsonPath("$.attributes.img.id").value(3))
                .andExpect(jsonPath("$.attributes.img.name").value("selfie.png"))
                .andExpect(jsonPath("$.attributes.addy.id").value(1))
                .andExpect(jsonPath("$.attributes.addy.streetAddress").value("1 My St."));
    }

    @Test
    public void testGetPlayerWithImageAndAddress() throws Exception {
        String response = performJoinWithImageAndAddress("player 1").andReturn().getResponse().getContentAsString();
        Integer playerId = JsonPath.compile("$.id").read(response);
        perform(get("/game/{url}/player/{id}", "needs_image_and_address", playerId)
                .with(user("player1@player.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.name").value("player 1"))
                .andExpect(jsonPath("$.attributes.img.id").value(3))
                .andExpect(jsonPath("$.attributes.addy.id").value(1));
    }

    @Test
    public void testUpdatePlayerImageAndAddress() throws Exception {
        String name = "PlayerKiller";
        String response = performJoinWithImageAndAddress(name).andReturn().getResponse().getContentAsString();
        Integer playerId = JsonPath.compile("$.id").read(response);

        performUpdatePlayerObjectAttr(playerId, "img", 4)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.attributes.img.id").value(4))
                .andExpect(jsonPath("$.attributes.addy.id").value(1));

        performUpdatePlayerObjectAttr(playerId, "addy", 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.attributes.img.id").value(4))
                .andExpect(jsonPath("$.attributes.addy.id").value(2));
    }

    @Test
    public void testUpdatePlayerNameAndTextAttribute() throws Exception {
        String name = "PlayerKiller";
        String response = performJoinWithImageAndAddress(name).andReturn().getResponse().getContentAsString();
        Integer playerId = JsonPath.compile("$.id").read(response);

        performUpdatePlayerNameAndTextAttr(playerId, "new name", "text", "pizza")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.attributes.text").value("pizza"));
    }

    private ResultActions performUpdatePlayerObjectAttr(Integer playerId, String field, int id) throws Exception {
        return mockMvc.perform(
                put("/game/{url}/player/{id}", "needs_image_and_address", playerId)
                        .with(user("player1@player.com"))
                        .content(jsonBuilder().addChild("attributes",
                                jsonBuilder().addChild(field, jsonBuilder().add("id", id))).build()));
    }

    private ResultActions performUpdatePlayerNameAndTextAttr(Integer playerId, String name, String field, String value) throws Exception {
        return mockMvc.perform(
                put("/game/{url}/player/{id}", "needs_image_and_address", playerId)
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("name", name)
                                .addChild("attributes",
                                        jsonBuilder().add(field, value)).build()));
    }

    private ResultActions performJoinWithImageAndAddress(String name) throws Exception {
        return mockMvc.perform(
                post("/game/{url}/player", "needs_image_and_address")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("name", name)
                                .addChild("attributes",
                                        jsonBuilder()
                                                .addChild("img", jsonBuilder().add("id", 3))
                                                .addChild("addy", jsonBuilder().add("id", 1))
                                                .add("text", "Baby"))
                                .build()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
