package com.subrosagames.subrosa.api.web;

import java.util.function.Consumer;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
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
    public void testJoinGameWithoutPlayerId() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "fun_times")
                        .with(user("player1@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("playerId", "required"))));
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
    public void testJoinGameWithUserUnder13WithAgeRestriction() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "must_be_18")
                        .with(user("child@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasSize(1)))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("age", "atLeast", "13"))));
    }

    @Test
    public void testJoinGameWithUserUnder13RegardlessOfRestrictions() throws Exception {
        mockMvc.perform(
                post("/game/{url}/player", "fun_times")
                        .with(user("child@player.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasSize(1)))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("age", "atLeast", "13"))));
    }

    @Test
    public void testJoinGameMissingRequiredAttribute() throws Exception {
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
        int playerId = 1;
        mockMvc.perform(
                post("/game/{url}/player", "last_wish_required")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("playerId", playerId)
                                .addChild("attributes",
                                        jsonBuilder()
                                                .add("lastWish", lastWish))
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.player.id").value(1))
                .andExpect(jsonPath("$.attributes.lastWish").value(lastWish));
    }

    @Test
    public void testJoinGameWithOptionalAttribute() throws Exception {
        String lastWish = "You don't have to do this. You can still let me win.";
        int playerId = 1;
        mockMvc.perform(
                post("/game/{url}/player", "last_wish_optional")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("playerId", playerId)
                                .addChild("attributes",
                                        jsonBuilder()
                                                .add("lastWish", lastWish))
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.player.id").value(1))
                .andExpect(jsonPath("$.attributes.lastWish").value(lastWish));
    }

    @Test
    public void testJoinGameWithoutOptionalAttribute() throws Exception {
        int playerId = 1;
        mockMvc.perform(
                post("/game/{url}/player", "last_wish_optional")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("playerId", playerId)
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.player.id").value(1))
                .andExpect(jsonPath("$.attributes.lastWish").doesNotExist());
    }

    @Test
    public void testJoinGameMissingPlayerId() throws Exception {
        performJoinWithImageAndAddress(null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("playerId", "required"))));
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
        int playerId = 1;
        performJoinWithImageAndAddress(playerId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.player.id").value(playerId))
                .andExpect(jsonPath("$.attributes.img").exists())
                .andExpect(jsonPath("$.attributes.img.id").value(3))
                .andExpect(jsonPath("$.attributes.img.name").value("selfie.png"))
                .andExpect(jsonPath("$.attributes.addy.id").value(1))
                .andExpect(jsonPath("$.attributes.addy.streetAddress").value("1 My St."));
    }

    @Test
    @Ignore("this is not yet enforced")
    public void joinGame_whenAlreadyAPlayer_resultsInConflict() throws Exception {
        performJoin("fun_times", "player1@player.com", 1, (ResultActions actions) -> {
            try {
                actions.andExpect(status().isCreated()).andExpect(jsonPath("$.player.id").value(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        performJoin("fun_times", "player1@player.com", 10, actions -> {
            try {
                actions.andExpect(status().isConflict());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void performJoin(String gameUrl, String user, int playerId, Consumer<ResultActions> expectations) throws Exception {
        expectations.accept(
                mockMvc.perform(
                        post("/game/{url}/player", gameUrl)
                                .with(user(user))
                                .content(jsonBuilder().add("playerId", playerId).build()))
        );
    }

    @Test
    public void testGetPlayerWithImageAndAddress() throws Exception {
        String response = performJoinWithImageAndAddress(1).andReturn().getResponse().getContentAsString();
        Integer playerId = JsonPath.compile("$.id").read(response);
        perform(get("/game/{url}/player/{id}", "needs_image_and_address", playerId)
                .with(user("player1@player.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.name").value("peter"))
                .andExpect(jsonPath("$.player.id").value(1))
                .andExpect(jsonPath("$.attributes.img.id").value(3))
                .andExpect(jsonPath("$.attributes.addy.id").value(1));
    }

    @Test
    public void testUpdatePlayerImageAndAddress() throws Exception {
        int playerId = 1;
        String response = performJoinWithImageAndAddress(playerId).andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.compile("$.id").read(response);

        performUpdatePlayerObjectAttr(id, "img", 4)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.player.id").value(playerId))
                .andExpect(jsonPath("$.attributes.img.id").value(4))
                .andExpect(jsonPath("$.attributes.addy.id").value(1));

        performUpdatePlayerObjectAttr(id, "addy", 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.player.id").value(playerId))
                .andExpect(jsonPath("$.attributes.img.id").value(4))
                .andExpect(jsonPath("$.attributes.addy.id").value(2));
    }

    @Test
    public void testUpdatePlayerTextAttribute() throws Exception {
        String response = performJoinWithImageAndAddress(1).andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.compile("$.id").read(response);

        performUpdatePlayerTextAttr(id, "text", "pizza")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.attributes.text").value("pizza"));
    }

    private ResultActions performUpdatePlayerObjectAttr(Integer playerId, String field, int id) throws Exception {
        return mockMvc.perform(
                put("/game/{url}/player/{id}", "needs_image_and_address", playerId)
                        .with(user("player1@player.com"))
                        .content(jsonBuilder().addChild("attributes",
                                jsonBuilder().addChild(field, jsonBuilder().add("id", id))).build()));
    }

    private ResultActions performUpdatePlayerTextAttr(Integer playerId, String field, String value) throws Exception {
        return mockMvc.perform(
                put("/game/{url}/player/{id}", "needs_image_and_address", playerId)
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .addChild("attributes",
                                        jsonBuilder().add(field, value)).build()));
    }

    private ResultActions performJoinWithImageAndAddress(Integer playerId) throws Exception {
        return mockMvc.perform(
                post("/game/{url}/player", "needs_image_and_address")
                        .with(user("player1@player.com"))
                        .content(jsonBuilder()
                                .add("playerId", playerId)
                                .addChild("attributes", jsonBuilder()
                                        .addChild("img", jsonBuilder().add("id", 3))
                                        .addChild("addy", jsonBuilder().add("id", 1))
                                        .add("text", "Baby"))
                                .build()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
