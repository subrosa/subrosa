package com.subrosagames.subrosa.api.web;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameStatus;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.game.support.assassin.AssassinGame;
import com.subrosagames.subrosa.event.ScheduledEvent;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailField.withDetailField;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link com.subrosagames.subrosa.api.web.ApiGameController}.
 */
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameControllerTest extends AbstractApiControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameControllerTest.class);

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameFactory gameFactory;

    @Test
    public void testGameRetrieval() throws Exception {
        BaseGame game = new AssassinGame();
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
    public void testGameRetrievalInvalidExpansionIsSuccessful() throws Exception {
        mockMvc.perform(
                get("/game/fun_times?expand=dne"))
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
        BaseGame entity1 = gameRepository.get(active1, "events");
        entity1.setGameRepository(gameRepository);
        entity1.setGameFactory(gameFactory);
        addEventToGame(entity1, "registrationStart", new Date(timeDaysInFuture(-1)));
        addEventToGame(entity1, "registrationEnd", new Date(timeDaysInFuture(1)));
        String active2 = createGame(new HashMap<String, String>() {
            {
                put("name", "active 2");
                put("description", "description");
                put("gameType", "ASSASSIN");
            }
        });
        BaseGame entity2 = gameRepository.get(active2, "events");
        entity2.setGameRepository(gameRepository);
        entity2.setGameFactory(gameFactory);
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
        BaseGame entity3 = gameRepository.get(inactive1, "events");
        entity3.setGameRepository(gameRepository);
        entity3.setGameFactory(gameFactory);
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
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameType", "required"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("gameType", "ASSASSIN").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameType", "required"))));

        mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "name of the game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name of the game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"));
    }

    @Test
    public void testGameCreationSetsDefaultValues() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "name of the game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0.0))
                .andExpect(jsonPath("$.maximumTeamSize").value(0));
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
        String response = mockMvc.perform(post("/game").with(user("new@user.com"))
                .content(jsonBuilder().add("name", "game to update").add("gameType", "SCAVENGER").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("game to update"))
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(put("/game/{url}", url).with(user("new@user.com"))
                .content(jsonBuilder().add("name", "renamed game").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));

        mockMvc.perform(get("/game/{url}", url).with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));
    }

    @Test
    public void testGameUpdateByAdmin() throws Exception {
        mockMvc.perform(put("/game/{url}", "fun_times").with(user("admin@user.com"))
                .content(jsonBuilder().add("name", "this is now the name").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("this is now the name"));
    }

    @Test
    public void testGameUpdateWithWrongAccount() throws Exception {
        mockMvc.perform(put("/game/{url}", "fun_times").with(user("new@user.com"))
                .content(jsonBuilder().add("name", "this is now the name").build()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value(notificationList()));
    }

    @Test
    public void testUpdateWithImage() throws Exception {
        String game = mockMvc.perform(get("/game/{url}", "with_image").with(user("game@owner.com")))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(put("/game/{url}", "with_image").with(user("game@owner.com")).content(game))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image.name").value("pic1.png"));

        mockMvc.perform(put("/game/{url}", "with_image").with(user("game@owner.com"))
                .content(jsonBuilder().add("imageId", 2).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image.name").value("pic2.png"));

        mockMvc.perform(get("/game/{url}", "with_image").with(user("game@owner.com")))
                .andExpect(jsonPath("$.image.name").value("pic2.png"));
    }

    @Test
    public void testUpdateRemoveImage() throws Exception {
        mockMvc.perform(put("/game/{url}", "with_image").with(user("game@owner.com"))
                .content(jsonBuilder().add("imageId", null).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").doesNotExist());

        mockMvc.perform(get("/game/{url}", "with_image").with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").doesNotExist());
    }

    @Test
    public void testCreateWithImage() throws Exception {
        mockMvc.perform(post("/game").with(user("game@owner.com"))
                .content(jsonBuilder().add("name", "my favorite game").add("gameType", "ASSASSIN").add("imageId", 1).build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.image.name").value("pic1.png"));
    }

    @Test
    public void testCreateWithImageNotFound() throws Exception {
        mockMvc.perform(post("/game").with(user("game@owner.com"))
                .content(jsonBuilder().add("name", "my favorite game").add("gameType", "ASSASSIN").add("imageId", 666).build()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateWithInvalidGameType() throws Exception {
        mockMvc.perform(post("/game").with(user("game@owner.com"))
                .content(jsonBuilder().add("name", "my favorite game").add("gameType", "WTF_IS_THIS").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameType", "unknown"))));
    }

    @Test
    public void testCreateAndUpdateGamePlayerInfoRequirements() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder()
                                .add("name", "Game with Player Info")
                                .add("gameType", "SCAVENGER")
                                .addArray("playerInfo",
                                        jsonBuilder().add("name", "Last Wish").add("description", "Your dying request").add("type", "text"),
                                        jsonBuilder().add("name", "Nude Photo").add("description", "We need more of these").add("type", "image")
                                ).build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerInfo").value(hasSize(2)))
                .andExpect(jsonPath("$.playerInfo[0].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[0].name").value("Last Wish"))
                .andExpect(jsonPath("$.playerInfo[1].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[1].name").value("Nude Photo"))
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);
        String lastWishId = JsonPath.compile("$.playerInfo[0].fieldId").read(response);
        String nudePhotoId = JsonPath.compile("$.playerInfo[1].fieldId").read(response);

        mockMvc.perform(get("/game/{url}", url).with(user("new@user.com")))
                .andExpect(jsonPath("$.playerInfo").value(hasSize(2)))
                .andExpect(jsonPath("$.playerInfo[0].fieldId").value(lastWishId))
                .andExpect(jsonPath("$.playerInfo[0].name").value("Last Wish"))
                .andExpect(jsonPath("$.playerInfo[1].fieldId").value(nudePhotoId))
                .andExpect(jsonPath("$.playerInfo[1].name").value("Nude Photo"));

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(user("new@user.com"))
                        .content(jsonBuilder()
                                .add("name", "Game with Player Info")
                                .add("gameType", "SCAVENGER")
                                .addArray("playerInfo",
                                        jsonBuilder().add("name", "Nude Photo").add("description", "We need more of these").add("type", "image"),
                                        jsonBuilder().add("name", "Home Address")
                                                .add("description", "Yes, we are asking for a nudey and your home address").add("type", "address"),
                                        jsonBuilder().add("name", "事務所").add("description", "働く所").add("type", "address")
                                ).build())).andExpect(status().isOk())
                .andExpect(jsonPath("$.playerInfo").value(hasSize(3)))
                .andExpect(jsonPath("$.playerInfo[0].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[0].name").value("Nude Photo"))
                .andExpect(jsonPath("$.playerInfo[1].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[1].name").value("Home Address"))
                .andExpect(jsonPath("$.playerInfo[2].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[2].name").value("事務所"));

        mockMvc.perform(get("/game/{url}", url).with(user("new@user.com")))
                .andExpect(jsonPath("$.playerInfo").value(hasSize(3)))
                .andExpect(jsonPath("$.playerInfo[0].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[0].name").value("Nude Photo"))
                .andExpect(jsonPath("$.playerInfo[1].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[1].name").value("Home Address"))
                .andExpect(jsonPath("$.playerInfo[2].fieldId").exists())
                .andExpect(jsonPath("$.playerInfo[2].name").value("事務所"));
    }

    @Test
    public void testCannotUpdateIdOrGameType() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("name", "new game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);
        Integer id = JsonPath.compile("$.id").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(user("new@user.com"))
                        .content(jsonBuilder().add("id", 1234).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(
                put("/game/{url}", url)
                        .content(jsonBuilder().add("gameType", "SCAVENGER").build())
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"));
    }

    @Test
    @Ignore // TODO figure out how these should validate
    public void testCannotSetTimesInPast() throws Exception
    {
        String url = "fun_times";
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        Set<String> times = Sets.newHashSet("gameStart", "gameEnd", "registrationEnd");
        for (String time : times) {
            mockMvc.perform(
                    put("/game/{url}/", url)
                            .with(user("game@owner.com"))
                            .content(jsonBuilder().add(time, yesterday.getTimeInMillis()).build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value(is(notificationList())))
                    .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail(time, "future"))));
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
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameStart", "startBeforeEnd"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameEnd", "startBeforeEnd"))));
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(user("game@owner.com"))
                        .content(jsonBuilder().add("registrationEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("registrationStart", "startBeforeEnd"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("registrationEnd", "startBeforeEnd"))));
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
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameStart", "required"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameEnd", "required"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("registrationStart", "required"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("registrationEnd", "required"))));

        final Long registrationStart = timeDaysInFuture(1);
        final Long registrationEnd = timeDaysInFuture(7);
        final Long gameStart = timeDaysInFuture(7);
        final Long gameEnd = timeDaysInFuture(30);
        BaseGame gameEntity = gameRepository.get(url, "events");
        gameEntity.setGameRepository(gameRepository);
        gameEntity.setGameFactory(gameFactory);
        addEventToGame(gameEntity, "registrationStart", new Date(registrationStart));
        addEventToGame(gameEntity, "registrationEnd", new Date(registrationEnd));
        addEventToGame(gameEntity, "gameStart", new Date(gameStart));
        addEventToGame(gameEntity, "gameEnd", new Date(gameEnd));
        Map<String, Object> updates = new HashMap<String, Object>() {
            {
                put("description", "it's going to be fun!");
            }
        };
        performGameUpdates(url, updates)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStart").value(gameStart))
                .andExpect(jsonPath("$.gameEnd").value(gameEnd))
                .andExpect(jsonPath("$.registrationStart").value(registrationStart))
                .andExpect(jsonPath("$.registrationEnd").value(registrationEnd));

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(notNullValue()));
    }

    @Test
    public void testPublishGameLocksUrl() throws Exception {
        String gameUrl = newRandomGame(GameType.SCAVENGER, "new@user.com");
        String updateResponse = mockMvc.perform(put("/game/{url}", gameUrl)
                .with(user("new@user.com"))
                .content(jsonBuilder().add("url", "new-url").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("new-url"))
                .andReturn().getResponse().getContentAsString();
        String newUrl = JsonPath.compile("$.url").read(updateResponse);

        BaseGame gameEntity = gameRepository.get(newUrl, "events");
        gameEntity.setGameRepository(gameRepository);
        gameEntity.setGameFactory(gameFactory);
        addEventToGame(gameEntity, "registrationStart", new Date(timeDaysInFuture(1)));
        addEventToGame(gameEntity, "registrationEnd", new Date(timeDaysInFuture(2)));
        addEventToGame(gameEntity, "gameStart", new Date(timeDaysInFuture(3)));
        addEventToGame(gameEntity, "gameEnd", new Date(timeDaysInFuture(4)));

        mockMvc.perform(
                post("/game/{url}/publish", newUrl)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());

        mockMvc.perform(put("/game/{url}", newUrl)
                .with(user("new@user.com"))
                .content(jsonBuilder().add("url", "even-newer-url").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("new-url"));
        mockMvc.perform(get("/game/{url}", "new-url").with(user("new@user.com"))).andExpect(status().isOk());
        mockMvc.perform(get("/game/{url}", "even-newer-url").with(user("new@user.com"))).andExpect(status().isNotFound());
    }

    @Test
    public void testGameUrlCollisionResultsInConflict() throws Exception {
        String game1 = newRandomGame(GameType.SCAVENGER, "new@user.com");
        String game2 = newRandomGame(GameType.SCAVENGER, "new@user.com");
        mockMvc.perform(put("/game/{url}", game2).with(user("new@user.com"))
                .content(jsonBuilder().add("url", game1).build()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("url", "unique"))));
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
                .andExpect(jsonPath("$.price").value(0))
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
        performGameUpdates(url, new HashMap<String, Object>())
                .andExpect(jsonPath("$.price").value(500.0));

        // update with null and see that it sets it successfully
        performGameUpdates(url, new HashMap<String, Object>() {
            {
                put("price", null);
            }
        })
                .andExpect(jsonPath("$.price").value(0));
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
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("content", "required"))));
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

        BaseGame baseGame = gameRepository.get(url, "events");
        baseGame.setGameRepository(gameRepository);
        baseGame.setGameFactory(gameFactory);
        addEventToGame(baseGame, "registrationStart", new Date(timeDaysInFuture(1)));
        addEventToGame(baseGame, "registrationEnd", new Date(timeDaysInFuture(2)));
        addEventToGame(baseGame, "gameStart", new Date(timeDaysInFuture(2)));
        addEventToGame(baseGame, "gameEnd", new Date(timeDaysInFuture(3)));

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(user("new@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GameStatus.PREREGISTRATION.name()));

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

    private void addEventToGame(BaseGame game, String event, Date date) throws Exception {
        GameEventDescriptor eventDescriptor = new GameEventDescriptor();
        eventDescriptor.setEvent(Optional.of(event));
        eventDescriptor.setDate(Optional.of(date));
        game.addEvent(eventDescriptor);
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

    private ResultActions performGameCreation(Map<String, Object> attributes) throws Exception {
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

    private ResultActions performGameUpdates(String url, Map<String, Object> updates) throws Exception {
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

    // CHECKSTYLE-ON: JavadocMethod

}