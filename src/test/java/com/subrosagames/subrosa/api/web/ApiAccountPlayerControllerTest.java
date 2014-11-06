package com.subrosagames.subrosa.api.web;

import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.ResultActions;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link ApiAccountPlayerController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountPlayerControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testListPlayers() throws Exception {
        checkListPlayerAssertions(mockMvc.perform(get("/account/3/player").with(user("lotsopics@user.com"))));
    }

    @Test
    public void testListPlayersForAuthenticatedUser() throws Exception {
        checkListPlayerAssertions(mockMvc.perform(get("/user/player").with(user("lotsopics@user.com"))));
    }

    private void checkListPlayerAssertions(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(paginatedList()))
                .andExpect(jsonPath("$").value(hasResultCount(2)))
                .andExpect(jsonPath("$.results[*].name").value(containsInAnyOrder("Player One!", "Secret Santa")));
    }

    @Test
    public void testListPlayersEmpty() throws Exception {
        mockMvc.perform(
                get("/account/1/player")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(paginatedList()))
                .andExpect(jsonPath("$").value(hasResultCount(0)));
    }

    @Test
    public void testListPlayersWrongAccount() throws Exception {
        mockMvc.perform(get("/account/3/player").with(user("bob@user.com"))).andExpect(status().isForbidden());
    }

    @Test
    public void testGetPlayer() throws Exception {
        checkGetPlayerAssertions(mockMvc.perform(get("/account/3/player/1").with(user("lotsopics@user.com"))),
                "Player One!", "pic1.png");
    }

    @Test
    public void testGetPlayerForAuthenticatedUser() throws Exception {
        checkGetPlayerAssertions(mockMvc.perform(get("/user/player/2").with(user("lotsopics@user.com"))),
                "Secret Santa", "pic2.png");
    }

    private void checkGetPlayerAssertions(ResultActions resultActions, String name, String imgName) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.image.name").value(imgName));
    }

    @Test
    public void testGetPlayerWrongAccount() throws Exception {
        mockMvc.perform(get("/account/3/player/2").with(user("bob@user.com"))).andExpect(status().isForbidden());
    }

    @Test
    public void testGetPlayerNotFound() throws Exception {
        mockMvc.perform(get("/account/3/player/5").with(user("lotsopics@user.com"))).andExpect(status().isNotFound());

    }

    @Test
    public void testCreatePlayer() throws Exception {
        checkNewPlayerProfileAssertions(
                mockMvc.perform(post("/account/1/player").with(user("bob@user.com")).content(playerProfileJson("my player", 5))),
                "my player", "img5.jpg");
    }

    @Test
    public void testCreatePlayerForAuthenticatedUser() throws Exception {
        checkNewPlayerProfileAssertions(
                mockMvc.perform(post("/user/player").with(user("bob@user.com")).content(playerProfileJson("I like games!", 5))),
                "I like games!", "img5.jpg");
    }

    void checkNewPlayerProfileAssertions(ResultActions resultActions, String name, String imageName) throws Exception {
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.image.name").value(imageName));
    }

    @Test
    public void testCreatePlayerWithWrongAccountForbidden() throws Exception {
        mockMvc.perform(post("/account/1/player").with(user("lotsopics@user.com")).content(playerProfileJson("name", 3))).andExpect(status().isForbidden());
    }

    @Test
    public void testCreatePlayerRequiresNameAndImage() throws Exception {
        mockMvc.perform(post("/account/3/player").with(user("lotsopics@user.com"))
                .content(playerProfileJson(null, 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))));

        mockMvc.perform(post("/account/3/player").with(user("lotsopics@user.com"))
                .content(playerProfileJson("name", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("image", "required"))));
    }

    String playerProfileJson(String name, Integer image) {
        return jsonBuilder()
                .add("name", name)
                .add("imageId", image)
                .build();
    }

    @Test
    public void testUpdatePlayer() throws Exception {
        checkUpdatePlayerProfileAssertions(mockMvc.perform(put("/account/3/player/2").with(user("lotsopics@user.com"))
                        .content(playerProfileJson("new name", 3))),
                "new name", "pic3.png");
    }

    @Test
    public void testUpdatePlayerForAuthenticatedUser() throws Exception {
        checkUpdatePlayerProfileAssertions(mockMvc.perform(put("/user/player/2").with(user("lotsopics@user.com")).content(playerProfileJson("updated", 2))),
                "updated", "pic2.png");
    }

    private void checkUpdatePlayerProfileAssertions(ResultActions resultActions, String name, String imageName) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.image.name").value(imageName));
    }

    @Test
    public void testUpdatePlayerImageNotFound() throws Exception {
        mockMvc.perform(put("/account/3/player/2").with(user("lotsopics@user.com")).content(playerProfileJson("player name", 666)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePlayerWithCreateResponse() throws Exception {
        String createdPlayer = mockMvc.perform(post("/user/player").with(user("bob@user.com")).content(playerProfileJson("my player", 5)))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.compile("$.id").read(createdPlayer);
        mockMvc.perform(put("/user/player/{id}", id).with(user("bob@user.com")).content(createdPlayer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("my player"))
                .andExpect(jsonPath("$.image.id").value(5));
    }

    @Test
    public void testDeletePlayer() throws Exception {
        mockMvc.perform(delete("/account/3/player/2").with(user("lotsopics@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        mockMvc.perform(get("/account/3/player/2").with(user("lotsopics@user.com"))).andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePlayerForAuthenticatedUser() throws Exception {
        mockMvc.perform(delete("/account/3/player/1").with(user("lotsopics@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/account/3/player/1").with(user("lotsopics@user.com"))).andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePlayerNotFound() throws Exception {
        mockMvc.perform(delete("/account/3/player/666").with(user("lotsopics@user.com"))).andExpect(status().isNotFound());
    }

    // CHECKSTYLE-ON: JavadocMethod
}
