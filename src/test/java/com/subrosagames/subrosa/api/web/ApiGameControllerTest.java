package com.subrosagames.subrosa.api.web;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
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
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import com.subrosagames.subrosa.event.ScheduledEvent;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasItems;
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
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailField.withDetailField;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link com.subrosagames.subrosa.api.web.ApiGameController}.
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
    public void testFindActiveGames() throws Exception {
        Long now = new Date().getTime();

        // precondition - no active games
        mockMvc.perform(
                get("/game")
                        .param("registrationStartBefore", now.toString())
                        .param("registrationEndAfter", now.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultCount(0)));

        // create 2 active and an inactive game
        String active1 = createGame(new HashMap<String, String>() {
            {
                put("name", "active 1");
                put("description", "description");
                put("gameType", "ASSASSIN");
            }
        });
        GameEntity entity1 = gameRepository.get(active1, "events");
        entity1.setGameRepository(gameRepository);
        addEventToGame(entity1, "registrationStart", new Date(timeDaysInFuture(-1)));
        addEventToGame(entity1, "registrationEnd", new Date(timeDaysInFuture(1)));
        String active2 = createGame(new HashMap<String, String>() {
            {
                put("name", "active 2");
                put("description", "description");
                put("gameType", "ASSASSIN");
            }
        });
        GameEntity entity2 = gameRepository.get(active2, "events");
        entity2.setGameRepository(gameRepository);
        addEventToGame(entity2, "registrationStart", new Date(timeDaysInFuture(-100)));
        addEventToGame(entity2, "registrationEnd", new Date(timeDaysInFuture(100)));
        String inactive1 = createGame(new HashMap<String, String>() {
            {
                put("name", "inactive 1");
                put("description", "description");
                put("gameType", "ASSASSIN");
                put("registrationStart", Long.toString(timeDaysInFuture(1)));
                put("registrationEnd", Long.toString(timeDaysInFuture(5)));
            }
        });
        GameEntity entity3 = gameRepository.get(inactive1, "events");
        entity3.setGameRepository(gameRepository);
        addEventToGame(entity3, "registrationStart", new Date(timeDaysInFuture(1)));
        addEventToGame(entity3, "registrationEnd", new Date(timeDaysInFuture(5)));

        // search should now contain the two active games
        mockMvc.perform(
                get("/game")
                        .param("registrationStartBefore", now.toString())
                        .param("registrationEndAfter", now.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultCount(2)))
                .andExpect(jsonPath("$").value(hasResultsSize(2)))
                .andExpect(jsonPath("$.results[*].url").value(hasItems(active1, active2)));
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
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("gameType"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("gameType", "ASSASSIN").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("name"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("name"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("gameType"))));

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
                .andExpect(jsonPath("$.url").value(url))
        ;
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
    @Ignore // TODO figure out how these should validate
    public void testCannotSetTimesInPast() throws Exception
    {
        String url = "fun_times";
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        HashSet<String> times = Sets.newHashSet("gameStart", "gameEnd", "registrationEnd");
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
    @Ignore // TODO figure out how these should validate
    public void testEndTimesMustBeAfterStartTimes() throws Exception
    {
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
    @Ignore // TODO figure out how these should validate
    public void testRegistrationEndAtOrBeforeGameStart() throws Exception
    {
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
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("gameStart"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("registrationEnd"))));
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
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("gameStart"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("gameEnd"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("registrationStart"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailField("registrationEnd"))));

        final Long registrationStart = timeDaysInFuture(1);
        final Long registrationEnd = timeDaysInFuture(7);
        final Long gameStart = timeDaysInFuture(7);
        final Long gameEnd = timeDaysInFuture(30);
        GameEntity gameEntity = gameRepository.get(url, "events");
        gameEntity.setGameRepository(gameRepository);
        addEventToGame(gameEntity, "registrationStart", new Date(registrationStart));
        addEventToGame(gameEntity, "registrationEnd", new Date(registrationEnd));
        addEventToGame(gameEntity, "gameStart", new Date(gameStart));
        addEventToGame(gameEntity, "gameEnd", new Date(gameEnd));
        HashMap<String, Object> updates = new HashMap<String, Object>() {
            {
                put("description", "it's going to be fun!");
            }
        };
        performGameUpdates(url, updates)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStart").value(gameStart))
                .andExpect(jsonPath("$.gameEnd").value(gameEnd))
                .andExpect(jsonPath("$.registrationStart").value(registrationStart))
                .andExpect(jsonPath("$.registrationEnd").value(registrationEnd))
        ;

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(notNullValue()))
        ;
    }

    @Test
    public void testNullPriceMakesFreeGame() throws Exception {
        String response = performGameCreation(new HashMap<String, Object>() {
            {
                put("name", "name of the game");
                put("gameType", "ASSASSIN");
                put("price", null);
            }
        })
                .andExpect(jsonPath("$.price").value(nullValue()))
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        // enter a price and assert it updates successfully
        performGameUpdates(url, new HashMap<String, Object>() {
            {
                put("price", 500);
            }
        })
                .andExpect(jsonPath("$.price").value(500));

        // update without specifying price and see original
        performGameUpdates(url, new HashMap<String, Object>() {{
        }})
                .andExpect(jsonPath("$.price").value(500.0));

        // update with null and see that it sets it successfully
        performGameUpdates(url, new HashMap<String, Object>() {
            {
                put("price", null);
            }
        })
                .andExpect(jsonPath("$.price").value(nullValue()));
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
        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .with(user("new@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .content("{}")
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

        performGameUpdates(url, new HashMap<String, Object>() {
            {
                put("description", "need this");
            }
        });

        GameEntity gameEntity = gameRepository.get(url, "events");
        gameEntity.setGameRepository(gameRepository);
        addEventToGame(gameEntity, "registrationStart", new Date(timeDaysInFuture(1)));
        addEventToGame(gameEntity, "registrationEnd", new Date(timeDaysInFuture(2)));
        addEventToGame(gameEntity, "gameStart", new Date(timeDaysInFuture(2)));
        addEventToGame(gameEntity, "gameEnd", new Date(timeDaysInFuture(3)));

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GameStatus.PREREGISTRATION.name()))
        ;

        updateEvent(gameRepository.get(url).getRegistrationStartEvents().get(0), new Date(timeDaysInFuture(-4)));
        LOG.debug("registrationStart: {}", gameRepository.get(url).getRegistrationStart());
        expectGameStatusIs(url, GameStatus.REGISTRATION);

        updateEvent(gameRepository.get(url).getRegistrationEndEvents().get(0), new Date(timeDaysInFuture(-3)));
        expectGameStatusIs(url, GameStatus.POSTREGISTRATION);

        updateEvent(gameRepository.get(url).getGameStartEvents().get(0), new Date(timeDaysInFuture(-2)));
        expectGameStatusIs(url, GameStatus.RUNNING);

        updateEvent(gameRepository.get(url).getGameEndEvents().get(0), new Date(timeDaysInFuture(-1)));
        expectGameStatusIs(url, GameStatus.ARCHIVED);
    }

    private void addEventToGame(GameEntity game, String event, Date date) throws Exception {
        ScheduledEventEntity scheduledEvent = new ScheduledEventEntity();
        scheduledEvent.setEvent(event);
        scheduledEvent.setDate(date);
        game.addEvent(scheduledEvent);
    }

    private void updateEvent(ScheduledEvent event, Date date) throws Exception {
        ScheduledEventEntity eventEntity = (ScheduledEventEntity) gameRepository.getEvent(event.getId());
        eventEntity.setDate(date);
        gameRepository.update(eventEntity);
    }

    private void expectGameStatusIs(String url, GameStatus gameStatus) throws Exception {
        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(jsonPath("$.status").value(gameStatus.name()));
    }

    private ResultActions performGameCreation(HashMap<String, Object> attributes) throws Exception {
        JsonBuilder builder = jsonBuilder();
        for (Map.Entry<String, Object> update : attributes.entrySet()) {
            builder.add(update.getKey(), update.getValue());
        }
        String json = builder.build();
        return mockMvc.perform(
                post("/game")
                        .content(json)
                        .with(user("new@user.com")))
                .andExpect(status().isCreated());
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

    private String createGame(Map<String, String> properties) throws Exception {
        JsonBuilder jsonBuilder = jsonBuilder();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            jsonBuilder.add(property.getKey(), property.getValue());
        }
        String response = mockMvc.perform(
                post("/game")
                        .content(jsonBuilder.build())
                        .with(user("new@user.com")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.compile("$.url").read(response);
    }

    @Test
    public void testTest() throws Exception {
        GameEntity gameEntity = gameRepository.get("with_start");
        Date gameStart = gameEntity.getGameStart();
        LOG.debug("Game start: {}", gameStart);
    }


    // CHECKSTYLE-ON: JavadocMethod

}
