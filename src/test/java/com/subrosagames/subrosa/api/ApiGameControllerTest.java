package com.subrosagames.subrosa.api;

import java.util.Calendar;

import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultsSize.hasResultsSize;
import static com.subrosagames.subrosa.test.matchers.NotificationListContents.NotificationDetailKey.withDetailKey;
import static com.subrosagames.subrosa.test.matchers.NotificationListContents.NotificationListHas.hasNotification;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link com.subrosagames.subrosa.api.ApiGameController}.
 */
@TestExecutionListeners({
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameControllerTest extends AbstractApiControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void testGameRetrieval() throws Exception {
        GameEntity game = new AssassinGame();
        game.setName("Test game");
        game.setUrl("test_game_url");
        game.setGameType(GameType.ASSASSIN);
        Lifecycle lifecycle = new LifecycleEntity();
        gameRepository.save(lifecycle);
        game.setLifecycle(lifecycle);
        gameRepository.create(game);

        mockMvc.perform(
                get("/game/test_game_url")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Test game"));

        mockMvc.perform(
                get("/game/fun_times")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Fun Times!"));

    }

    @Test
    public void testGameNotFound() throws Exception {
        mockMvc.perform(get("/game/does_not_exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testListGamesNoArgumentsDefaultsTo10() throws Exception {
        mockMvc.perform(get("/game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultsSize(10)));
    }

    @Test
    public void testListGamesWithOffsetAndLimit() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/game")
                        .param("limit", "5")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultsSize(5)))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Integer count = JsonPath.compile("$.resultCount").read(response);

        mockMvc.perform(
                get("/game")
                        .param("limit", "5")
                        .param("offset", String.valueOf(count - 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultCount(count)))
                .andExpect(jsonPath("$").value(hasResultsSize(2)));
    }

    @Test
    public void testGameCreationRequiresAuth() throws Exception {
        mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"game to update\", \"gameType\": \"SCAVENGER\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testGameCreationRequiresNameAndType() throws Exception {
        mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"name of the game\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameType"))));

        mockMvc.perform(
                post("/game")
                        .content("{\"gameType\": \"ASSASSIN\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("name"))));

        mockMvc.perform(
                post("/game")
                        .content("{}")
                        .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("name"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameType"))));

        mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"name of the game\", \"gameType\": \"ASSASSIN\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name of the game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"));
    }

    @Test
    public void testGameCreation() throws Exception {
        mockMvc.perform(
                get("/user/game")
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(0)));

        String response = mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"my favorite game\", \"gameType\": \"ASSASSIN\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("my favorite game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"))
                .andExpect(jsonPath("$.url").value(not(isEmptyOrNullString())))
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                get("/user/game")
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)));

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("my favorite game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"))
                .andExpect(jsonPath("$.url").value(url));
    }

    @Test
    public void testGameUpdate() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"game to update\", \"gameType\": \"SCAVENGER\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("game to update"))
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .content("{\"name\": \"renamed game\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));
    }

    @Test
    public void testCannotUpdateUrlOrGameType() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"new game\", \"gameType\": \"ASSASSIN\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .content("{\"url\": \"new url\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(url));

        mockMvc.perform(
                put("/game/{url}", url)
                        .content("{\"gameType\": \"SCAVENGER\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(url));
    }

//    @Test
    public void testCannotSetStartTimeInPast() throws Exception {
        String url = "fun_times";
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format("{\"startTime\": \"%s\"}", yesterday.getTimeInMillis())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format("{\"registrationStartTime\": \"%s\"}", yesterday.getTimeInMillis())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

//    @Test
    public void testCannotSetEndTimeBeforeStart() throws Exception {
        String url = "fun_times";
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.DATE, 30);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format(
                                "{\"startTime\": \"%s\", \"registrationStartTime\": \"%s\"}",
                                nextMonth.getTimeInMillis(), nextMonth.getTimeInMillis())
                        ))
                .andExpect(status().isOk());

        nextMonth.add(Calendar.DATE, -1);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format("{\"endTime\": \"%s\"}", nextMonth.getTimeInMillis())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format("{\"registrationEndTime\": \"%s\"}", nextMonth.getTimeInMillis())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

//    @Test
    public void testRegistrationMustEndBeforeGameStarts() throws Exception {
        String url = "fun_times";
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.DATE, 30);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format("{\"startTime\": \"%s\"}", nextMonth.getTimeInMillis())))
                .andExpect(status().isOk());

        nextMonth.add(Calendar.DATE, 1);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(String.format("{\"registrationEndTime\": \"%s\"}", nextMonth.getTimeInMillis())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testPublishGame() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .content("{\"name\": \"game to publish\", \"gameType\": \"ASSASSIN\"}")
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("description"))));

        /*
        mockMvc.perform(
                put("/game/{url}", url)
                        .content(String.format("{\"description\": \"%s\"}", "it's going to be fun!"))
                        .with(user("new@user.com")))
                .andExpect(status().isOk());

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.DATE, 30);
        mockMvc.perform(
                put("/game/{url}", url)
                        .content(String.format("{\"startTime\": \"%s\", \"endTime\": \"%s\"}", tomorrow.getTimeInMillis(), nextMonth.getTimeInMillis()))
                        .with(user("new@user.com")))
                .andExpect(status().isOk());

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());
                */
    }
}
