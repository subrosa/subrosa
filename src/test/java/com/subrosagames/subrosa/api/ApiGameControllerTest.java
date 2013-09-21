package com.subrosagames.subrosa.api;

import java.util.Calendar;
import java.util.HashSet;

import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultsSize.hasResultsSize;
import static com.subrosagames.subrosa.test.matchers.IsSortedList.isSortedAscending;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailKey.withDetailKey;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link com.subrosagames.subrosa.api.ApiGameController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void testGameRetrieval() throws Exception {
        GameEntity game = new AssassinGame();
        game.setName("Test game");
        game.setUrl("test_game_url");
        game.setGameType(GameType.ASSASSIN);
        gameRepository.create(game);

        mockMvc.perform(
                get("/game/test_game_url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test game"));

        mockMvc.perform(
                get("/game/fun_times"))
                .andExpect(status().isOk())
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
                        .content(jsonBuilder().add("name", "game to update").add("gameType", "SCAVENGER").build()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testGameCreationRequiresNameAndType() throws Exception {
        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "name of the game").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameType"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("gameType", "ASSASSIN").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("name"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("name"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameType"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "name of the game").add("gameType", "ASSASSIN").build()))
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
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "my favorite game").add("gameType", "ASSASSIN").build()))
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
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "game to update").add("gameType", "SCAVENGER").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("game to update"))
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "renamed game").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));
    }

    @Test
    public void testUnrecognizedFieldsResultsInBadRequest() throws Exception {
        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("what", "is this?").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("what"))));

        String response = mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "new game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("another", "weird param").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("another"))));
    }

    @Test
    public void testCannotUpdateUrlOrGameType() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "new game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("url", "new url").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(url));

        mockMvc.perform(
                put("/game/{url}", url)
                        .content(jsonBuilder().add("gameType", "SCAVENGER").build())
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(url));
    }

    @Test
    public void testCannotSetTimesInPast() throws Exception {
        String url = "fun_times";
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        HashSet<String> times = Sets.newHashSet("gameStart", "gameEnd", "registrationStart", "registrationEnd");
        for (String time : times) {
            mockMvc.perform(
                    put("/game/{url}/", url)
                            .with(user("game@owner.com"))
                            .content(jsonBuilder().add(time, yesterday.getTimeInMillis()).build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value(is(notificationList())));
        }
    }

    @Test
    public void testEndTimesMustBeAfterStartTimes() throws Exception {
        String url = "fun_times";
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.DATE, 30);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder()
                                .add("gameStart", nextMonth.getTimeInMillis())
                                .add("registrationStart", nextMonth.getTimeInMillis())
                                .build()))
                .andExpect(status().isOk());

        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder().add("gameEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder().add("registrationEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testRegistrationEndAtOrBeforeGameStart() throws Exception {
        String url = "fun_times";
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.DATE, 30);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder().add("gameStart", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isOk());

        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder().add("registrationEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isOk());

        nextMonth.add(Calendar.DATE, 1);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder().add("registrationEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameStart"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("registrationEnd"))));
    }

    @Test
    public void testPublishGame() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .content(jsonBuilder()
                                .add("name", "game to publish")
                                .add("gameType", "ASSASSIN").build())
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("description"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameStart"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("gameEnd"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("registrationStart"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("registrationEnd"))));

        final Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        final Calendar nextWeek = Calendar.getInstance();
        nextWeek.add(Calendar.DATE, 7);
        final Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.DATE, 30);
        mockMvc.perform(
                put("/game/{url}", url)
                        .content(jsonBuilder()
                                .add("description", "it's going to be fun!")
                                .add("gameStart", nextWeek.getTimeInMillis())
                                .add("gameEnd", nextMonth.getTimeInMillis())
                                .add("registrationStart", tomorrow.getTimeInMillis())
                                .add("registrationEnd", nextWeek.getTimeInMillis())
                                .build())
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStart").value(nextWeek.getTimeInMillis()))
                .andExpect(jsonPath("$.gameEnd").value(nextMonth.getTimeInMillis()))
                .andExpect(jsonPath("$.registrationStart").value(tomorrow.getTimeInMillis()))
                .andExpect(jsonPath("$.registrationEnd").value(nextWeek.getTimeInMillis()))
        ;

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());
    }

    @Test
    public void testCreatePostRequiresAuth() throws Exception {
        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .content(jsonBuilder().add("content", "new message").build()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testCreatePostMissingFields() throws Exception {
        mockMvc.perform(post("/game/{url}/post", "fun_times")
                .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testCreatePost() throws Exception {
        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("content", "new message").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("new message"));
    }

    @Test
    public void testGetPostsSortsByCreation() throws Exception {
        mockMvc.perform(get("/game/{url}/post", "fun_times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(2)))
                .andExpect(jsonPath("$.results[*].creationDate").value(isSortedAscending()));

        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("content", "new message").build()));

        mockMvc.perform(get("/game/{url}/post", "fun_times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(3)))
                .andExpect(jsonPath("$.results[*].creationDate").value(isSortedAscending()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
