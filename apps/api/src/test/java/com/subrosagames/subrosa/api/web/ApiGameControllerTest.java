package com.subrosagames.subrosa.api.web;

import java.util.Calendar;
import java.util.Collections;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameStatus;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.support.assassin.AssassinGame;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.lessThan;
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
import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.bearer;

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
        gameRepository.save(game);

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
        BaseGame entity1 = gameRepository.findOneByUrl(active1, "events").get();
        entity1.setRegistrationStart(new Date(timeDaysInFuture(-1)));
        entity1.setRegistrationEnd(new Date(timeDaysInFuture(1)));
        gameRepository.save(entity1);
        String active2 = createGame(new HashMap<String, String>() {
            {
                put("name", "active 2");
                put("description", "description");
                put("gameType", "ASSASSIN");
            }
        });
        BaseGame entity2 = gameRepository.findOneByUrl(active2, "events").get();
        entity2.setRegistrationStart(new Date(timeDaysInFuture(-100)));
        entity2.setRegistrationEnd(new Date(timeDaysInFuture(100)));
        gameRepository.save(entity2);
        String inactive1 = createGame(new HashMap<String, String>() {
            {
                put("name", "inactive 1");
                put("description", "description");
                put("gameType", "ASSASSIN");
                put("registrationStart", Long.toString(timeDaysInFuture(1)));
                put("registrationEnd", Long.toString(timeDaysInFuture(5)));
            }
        });
        BaseGame entity3 = gameRepository.findOneByUrl(inactive1, "events").get();
        entity3.setRegistrationStart(new Date(timeDaysInFuture(1)));
        entity3.setRegistrationEnd(new Date(timeDaysInFuture(5)));
        gameRepository.save(entity3);

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
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("name", "name of the game").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameType", "required"))));

        mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("gameType", "ASSASSIN").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))));

        mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("name", "required"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameType", "required"))));

        mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("name", "name of the game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name of the game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"));
    }

    @Test
    public void testGameCreationSetsDefaultValues() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("name", "name of the game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0.0))
                .andExpect(jsonPath("$.maximumTeamSize").value(0));
    }

    @Test
    public void testGameCreation() throws Exception {
        mockMvc.perform(
                get("/user/game")
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(0)));

        String response = mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("name", "my favorite game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("my favorite game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"))
                .andExpect(jsonPath("$.url").value(not(isEmptyOrNullString())))
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                get("/user/game")
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)));

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("my favorite game"))
                .andExpect(jsonPath("$.gameType").value("ASSASSIN"))
                .andExpect(jsonPath("$.url").value(url));
    }

    @Test
    public void testGameUpdate() throws Exception {
        String response = mockMvc.perform(post("/game").with(bearer(accessTokenForEmail("new@user.com")))
                .content(jsonBuilder().add("name", "game to update").add("gameType", "SCAVENGER").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("game to update"))
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(put("/game/{url}", url).with(bearer(accessTokenForEmail("new@user.com")))
                .content(jsonBuilder().add("name", "renamed game").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));

        mockMvc.perform(get("/game/{url}", url).with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed game"));
    }

    @Test
    public void testGameUpdateByAdmin() throws Exception {
        mockMvc.perform(put("/game/{url}", "fun_times").with(bearer(accessTokenForEmail("admin@user.com")))
                .content(jsonBuilder().add("name", "this is now the name").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("this is now the name"));
    }

    @Test
    public void testGameUpdateWithWrongAccount() throws Exception {
        mockMvc.perform(put("/game/{url}", "fun_times").with(bearer(accessTokenForEmail("new@user.com")))
                .content(jsonBuilder().add("name", "this is now the name").build()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value(notificationList()));
    }

    @Test
    public void testUpdateWithImage() throws Exception {
        String game = mockMvc.perform(get("/game/{url}", "with_image").with(bearer(accessTokenForEmail("game@owner.com"))))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(put("/game/{url}", "with_image").with(bearer(accessTokenForEmail("game@owner.com"))).content(game))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image.name").value("pic1.png"));

        mockMvc.perform(put("/game/{url}", "with_image").with(bearer(accessTokenForEmail("game@owner.com")))
                .content(jsonBuilder().add("imageId", 2).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image.name").value("pic2.png"));

        mockMvc.perform(get("/game/{url}", "with_image").with(bearer(accessTokenForEmail("game@owner.com"))))
                .andExpect(jsonPath("$.image.name").value("pic2.png"));
    }

    @Test
    public void testUpdateRemoveImage() throws Exception {
        mockMvc.perform(put("/game/{url}", "with_image").with(bearer(accessTokenForEmail("game@owner.com")))
                .content(jsonBuilder().add("imageId", null).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").doesNotExist());

        mockMvc.perform(get("/game/{url}", "with_image").with(bearer(accessTokenForEmail("game@owner.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").doesNotExist());
    }

    @Test
    public void testCreateWithImage() throws Exception {
        mockMvc.perform(post("/game").with(bearer(accessTokenForEmail("game@owner.com")))
                .content(jsonBuilder().add("name", "my favorite game").add("gameType", "ASSASSIN").add("imageId", 1).build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.image.name").value("pic1.png"));
    }

    @Test
    public void testCreateWithImageNotFound() throws Exception {
        mockMvc.perform(post("/game").with(bearer(accessTokenForEmail("game@owner.com")))
                .content(jsonBuilder().add("name", "my favorite game").add("gameType", "ASSASSIN").add("imageId", 666).build()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateWithInvalidGameType() throws Exception {
        mockMvc.perform(post("/game").with(bearer(accessTokenForEmail("game@owner.com")))
                .content(jsonBuilder().add("name", "my favorite game").add("gameType", "WTF_IS_THIS").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameType", "unknown"))));
    }

    @Test
    public void testCreateAndUpdateGamePlayerInfoRequirements() throws Exception {
        String response = mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder()
                                .add("name", "Game session Player Info")
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

        mockMvc.perform(get("/game/{url}", url).with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(jsonPath("$.playerInfo").value(hasSize(2)))
                .andExpect(jsonPath("$.playerInfo[0].fieldId").value(lastWishId))
                .andExpect(jsonPath("$.playerInfo[0].name").value("Last Wish"))
                .andExpect(jsonPath("$.playerInfo[1].fieldId").value(nudePhotoId))
                .andExpect(jsonPath("$.playerInfo[1].name").value("Nude Photo"));

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder()
                                .add("name", "Game session Player Info")
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

        mockMvc.perform(get("/game/{url}", url).with(bearer(accessTokenForEmail("new@user.com"))))
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
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("name", "new game").add("gameType", "ASSASSIN").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").exists())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);
        Integer id = JsonPath.compile("$.id").read(response);

        mockMvc.perform(
                put("/game/{url}", url)
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder().add("id", 1234).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(
                put("/game/{url}", url)
                        .content(jsonBuilder().add("gameType", "SCAVENGER").build())
                        .with(bearer(accessTokenForEmail("new@user.com"))))
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
                            .with(bearer(accessTokenForEmail("game@owner.com")))
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
                        .with(bearer(accessTokenForEmail("game@owner.com")))
                        .content(jsonBuilder()
                                .add("gameStart", nextMonth.getTimeInMillis())
                                .add("registrationStart", nextMonth.getTimeInMillis())
                                .build()))
                .andExpect(status().isOk());

        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(bearer(accessTokenForEmail("game@owner.com")))
                        .content(jsonBuilder().add("gameEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameStart", "startBeforeEnd"))))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameEnd", "startBeforeEnd"))));
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(bearer(accessTokenForEmail("game@owner.com")))
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
                        .with(bearer(accessTokenForEmail("game@owner.com")))
                        .content(jsonBuilder().add("gameStart", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isOk());

        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(bearer(accessTokenForEmail("game@owner.com")))
                        .content(jsonBuilder().add("registrationEnd", nextMonth.getTimeInMillis()).build()))
                .andExpect(status().isOk());

        nextMonth.add(Calendar.DATE, 1);
        mockMvc.perform(
                put("/game/{url}/", url)
                        .with(bearer(accessTokenForEmail("game@owner.com")))
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
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("gameStart", "required"))))
                .andExpect(jsonPath("$.notifications").value(not(hasNotification(withDetailField("registrationStart")))))
                .andExpect(jsonPath("$.notifications").value(not(hasNotification(withDetailField("gameEnd")))))
                .andExpect(jsonPath("$.notifications").value(not(hasNotification(withDetailField("registrationEnd")))));

        final Long registrationStart = timeDaysInFuture(1);
        final Long registrationEnd = timeDaysInFuture(7);
        final Long gameStart = timeDaysInFuture(7);
        final Long gameEnd = timeDaysInFuture(30);
        BaseGame gameEntity = gameRepository.findOneByUrl(url, "events").get();
        gameEntity = gameFactory.injectDependencies(gameEntity);
        gameEntity.setRegistrationStart(new Date(registrationStart));
        gameEntity.setRegistrationEnd(new Date(registrationEnd));
        gameEntity.setGameStart(new Date(gameStart));
        gameEntity.setGameEnd(new Date(gameEnd));
        gameRepository.save(gameEntity);

        performGameUpdates(url, ImmutableMap.of("description", "it's going to be fun!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameStart").value(gameStart))
                .andExpect(jsonPath("$.gameEnd").value(gameEnd))
                .andExpect(jsonPath("$.registrationStart").value(registrationStart))
                .andExpect(jsonPath("$.registrationEnd").value(registrationEnd));

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());

        mockMvc.perform(
                get("/game/{url}", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(notNullValue()));
    }

    @Test
    public void publishGame_WithoutRegistrationStart_FillsStartWithPublishTime() throws Exception {
        String url = newRandomGame(GameType.SCAVENGER, "new@user.com");
        BaseGame gameEntity = gameRepository.findOneByUrl(url, "events").get();
        gameFactory.injectDependencies(gameEntity);
        final Long gameStart = timeDaysInFuture(7);
        gameEntity.setGameStart(new Date(gameStart));
        gameRepository.save(gameEntity);

        mockMvc.perform(post("/game/{url}/publish", url)
                .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.registrationStart").value(lessThan(new Date().getTime())));
    }

    @Test
    public void testPublishGameLocksUrl() throws Exception {
        String gameUrl = newRandomGame(GameType.SCAVENGER, "new@user.com");
        String updateResponse = mockMvc.perform(put("/game/{url}", gameUrl)
                .with(bearer(accessTokenForEmail("new@user.com")))
                .content(jsonBuilder().add("url", "new-url").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("new-url"))
                .andReturn().getResponse().getContentAsString();
        String newUrl = JsonPath.compile("$.url").read(updateResponse);

        BaseGame gameEntity = gameRepository.findOneByUrl(newUrl, "events").get();
        gameEntity.setRegistrationStart(new Date(timeDaysInFuture(1)));
        gameEntity.setRegistrationEnd(new Date(timeDaysInFuture(2)));
        gameEntity.setGameStart(new Date(timeDaysInFuture(3)));
        gameEntity.setGameEnd(new Date(timeDaysInFuture(4)));
        gameRepository.save(gameEntity);

        mockMvc.perform(
                post("/game/{url}/publish", newUrl)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").exists());

        mockMvc.perform(put("/game/{url}", newUrl)
                .with(bearer(accessTokenForEmail("new@user.com")))
                .content(jsonBuilder().add("url", "even-newer-url").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("new-url"));
        mockMvc.perform(get("/game/{url}", "new-url").with(bearer(accessTokenForEmail("new@user.com")))).andExpect(status().isOk());
        mockMvc.perform(get("/game/{url}", "even-newer-url").with(bearer(accessTokenForEmail("new@user.com")))).andExpect(status().isNotFound());
    }

    @Test
    public void testGameUrlCollisionResultsInConflict() throws Exception {
        String game1 = newRandomGame(GameType.SCAVENGER, "new@user.com");
        String game2 = newRandomGame(GameType.SCAVENGER, "new@user.com");
        mockMvc.perform(put("/game/{url}", game2).with(bearer(accessTokenForEmail("new@user.com")))
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
        performGameUpdates(url, ImmutableMap.of("price", 500))
                .andExpect(jsonPath("$.price").value(500));

        // update without specifying price and see original
        performGameUpdates(url, new HashMap<>())
                .andExpect(jsonPath("$.price").value(500.0));

        // update session null and see that it sets it successfully
        performGameUpdates(url, Collections.singletonMap("price", null))
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
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .content("{}")
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("content", "required"))));
    }

    @Test
    public void testCreatePost() throws Exception {
        mockMvc.perform(
                post("/game/{url}/post", "fun_times")
                        .with(bearer(accessTokenForEmail("new@user.com")))
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
                        .with(bearer(accessTokenForEmail("new@user.com")))
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
                        .with(bearer(accessTokenForEmail("new@user.com")))
                        .content(jsonBuilder()
                                .add("name", "name of the game")
                                .add("gameType", "ASSASSIN")
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(GameStatus.DRAFT.name()))
                .andReturn().getResponse().getContentAsString();
        String url = JsonPath.compile("$.url").read(response);

        performGameUpdates(url, ImmutableMap.of("description", "need this"));

        BaseGame baseGame = gameRepository.findOneByUrl(url, "events").get();
        baseGame.setRegistrationStart(new Date(timeDaysInFuture(1)));
        baseGame.setRegistrationEnd(new Date(timeDaysInFuture(2)));
        baseGame.setGameStart(new Date(timeDaysInFuture(2)));
        baseGame.setGameEnd(new Date(timeDaysInFuture(3)));
        gameRepository.save(baseGame);

        mockMvc.perform(
                post("/game/{url}/publish", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GameStatus.PREREGISTRATION.name()));

        BaseGame game = gameRepository.findOneByUrl(url, "events").get();
        game.setRegistrationStart(new Date(timeDaysInFuture(-4)));
        gameRepository.save(game);
        expectGameStatusIs(url, GameStatus.REGISTRATION);

        game.setRegistrationEnd(new Date(timeDaysInFuture(-3)));
        gameRepository.save(game);
        expectGameStatusIs(url, GameStatus.POSTREGISTRATION);

        game.setGameStart(new Date(timeDaysInFuture(-2)));
        gameRepository.save(game);
        expectGameStatusIs(url, GameStatus.RUNNING);

        game.setGameEnd(new Date(timeDaysInFuture(-1)));
        gameRepository.save(game);
        expectGameStatusIs(url, GameStatus.ARCHIVED);
    }

    private void expectGameStatusIs(String url, GameStatus gameStatus) throws Exception {
        mockMvc.perform(
                get("/game/{url}", url)
                        .with(bearer(accessTokenForEmail("new@user.com"))))
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
                        .with(bearer(accessTokenForEmail("new@user.com"))))
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
                        .with(bearer(accessTokenForEmail("new@user.com"))))
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
                        .with(bearer(accessTokenForEmail("new@user.com"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.compile("$.url").read(response);
    }

    // CHECKSTYLE-ON: JavadocMethod

}
