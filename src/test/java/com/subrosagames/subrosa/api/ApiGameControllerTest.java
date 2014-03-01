package com.subrosagames.subrosa.api;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameStatus;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultsSize.hasResultsSize;
import static com.subrosagames.subrosa.test.matchers.IsSortedList.isSortedAscending;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailKey.withDetailKey;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link com.subrosagames.subrosa.api.ApiGameController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameControllerTest extends AbstractApiControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameControllerTest.class);

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

        final Long registrationStart = timeDaysInFuture(1);
        final Long registrationEnd = timeDaysInFuture(7);
        final Long gameStart = timeDaysInFuture(7);
        final Long gameEnd = timeDaysInFuture(30);
        HashMap<String, Object> updates = new HashMap<String, Object>() {{
            put("description", "it's going to be fun!");
            put("gameStart", gameStart);
            put("gameEnd", gameEnd);
            put("registrationStart", registrationStart);
            put("registrationEnd", registrationEnd);
        }};
        performGameUpdates(url, updates)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStart").value(gameStart))
                .andExpect(jsonPath("$.gameEnd").value(gameEnd))
                .andExpect(jsonPath("$.registrationStart").value(registrationStart))
                .andExpect(jsonPath("$.registrationEnd").value(gameStart))
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

    @Test
    public void testGameStatusFlags() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder()
                                .add("name", "name of the game")
                                .add("gameType", "ASSASSIN")
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(GameStatus.DRAFT.name()))
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        performGameUpdates(url, new HashMap<String, Object>() {{
            put("description", "need this");
            put("registrationStart", timeDaysInFuture(1));
            put("registrationEnd", timeDaysInFuture(2));
            put("gameStart", timeDaysInFuture(2));
            put("gameEnd", timeDaysInFuture(3));
        }});

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(GameStatus.PREREGISTRATION.name()))
        .andReturn().getResponse().getContentAsString();

        GameEntity gameEntity;
        gameEntity = gameRepository.get(url);
        gameEntity.setRegistrationStart(new Date(timeDaysInFuture(-4)));
        gameRepository.update(gameEntity);

        expectGameStatusIs(url, GameStatus.REGISTRATION);

        gameEntity = gameRepository.get(url);
        gameEntity.setRegistrationEnd(new Date(timeDaysInFuture(-3)));
        gameRepository.update(gameEntity);

        expectGameStatusIs(url, GameStatus.POSTREGISTRATION);

        gameEntity = gameRepository.get(url);
        gameEntity.setGameStart(new Date(timeDaysInFuture(-2)));
        gameRepository.update(gameEntity);

        expectGameStatusIs(url, GameStatus.RUNNING);

        gameEntity = gameRepository.get(url);
        gameEntity.setGameEnd(new Date(timeDaysInFuture(-1)));
        gameRepository.update(gameEntity);

        GameStatus gameStatus = GameStatus.ARCHIVED;
        expectGameStatusIs(url, gameStatus);
    }

    private void expectGameStatusIs(String url, GameStatus gameStatus) throws Exception {
        String contentAsString = mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
//                .andExpect(jsonPath("$.status").value(gameStatus.name()))
                .andReturn().getResponse().getContentAsString();
        return;
    }

    private ResultActions performGameUpdates(String url, HashMap<String, Object> updates) throws Exception {
        JsonBuilder builder = jsonBuilder();
        for (Map.Entry<String, Object> update : updates.entrySet()) {
            builder.add(update.getKey(), update.getValue());
        }
        String json = builder.build();
        return mockMvc.perform(
                put("/game/{url}", url)
                        .content(json)
                        .with(user("new@user.com")))
                .andExpect(status().isOk());
    }

    private long timeDaysInFuture(int days) {
        final Calendar registrationStart = Calendar.getInstance();
        registrationStart.add(Calendar.DATE, days);
        return registrationStart.getTimeInMillis();
    }

    // CHECKSTYLE-ON: JavadocMethod

}
