package com.subrosagames.subrosa.api.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;

/**
 * Test {@link ApiGameController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameEventControllerTest extends AbstractApiControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameEventControllerTest.class);

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testListEventsUnauthorized() throws Exception {
        mockMvc.perform(
                get("/game/fun_times/event"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testListEventsGameNotFound() throws Exception {
        mockMvc.perform(
                get("/game/does_not_exist/event")
                        .with(user("new@user.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                get("/game/does_not_exist/event")
                        .with(user("game@owner.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testListEventsEmpty() throws Exception {
        mockMvc.perform(
                get("/game/fun_times/event")
                        .with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(0)));
    }

    @Test
    public void testListEventsWithResults() throws Exception {
        mockMvc.perform(
                get("/game/with_start/event")
                        .with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(1)));
    }

    @Test
    public void testGetEventUnauthorized() throws Exception {
        mockMvc.perform(
                get("/game/with_start/event/{id}", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                get("/game/does_not_exist/event/{id}", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testGetEventForbidden() throws Exception {
        mockMvc.perform(
                get("/game/with_start/event/{id}", 1)
                        .with(user("new@user.com")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testGetEventGameNotFound() throws Exception {
        mockMvc.perform(
                get("/game/does_not_exist/event/{id}", 1)
                        .with(user("game@owner.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testGetEventNotFound() throws Exception {
        mockMvc.perform(
                get("/game/with_start/event/{id}", 666)
                        .with(user("game@owner.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testGetEvent() throws Exception {
        mockMvc.perform(
                get("/game/with_start/event/{id}", 1)
                        .with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.event").value("gameStart"));
    }

    @Test
    public void testCreateEventUnauthorized() throws Exception {
        mockMvc.perform(
                post("/game/fun_times/event")
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testCreateEventForbidden() throws Exception {
        mockMvc.perform(
                post("/game/fun_times/event")
                        .with(user("new@user.com"))
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testCreateEventBadRequest() throws Exception {
        mockMvc.perform(
                post("/game/fun_times/event")
                        .with(user("game@owner.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                post("/game/fun_times/event")
                        .with(user("game@owner.com"))
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                post("/game/fun_times/event")
                        .with(user("game@owner.com"))
                        .content(jsonBuilder()
                                .add("event", "gameEnd").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));

        mockMvc.perform(
                post("/game/fun_times/event")
                        .with(user("game@owner.com"))
                        .content(jsonBuilder()
                                .add("date", "2016-01-01").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testCreateEvent() throws Exception {
        mockMvc.perform(
                get("/game/fun_times/event")
                        .with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(0)));

        long date = timeDaysInFuture(300);
        String response = mockMvc.perform(
                post("/game/fun_times/event")
                        .with(user("game@owner.com"))
                        .content(jsonBuilder()
                                .add("event", "gameEnd")
                                .add("date", date)
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.compile("$.id").read(response);

        mockMvc.perform(
                get("/game/fun_times/event")
                        .with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(1)));

        mockMvc.perform(
                get("/game/fun_times/event/{id}", id)
                        .with(user("game@owner.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.event").value("gameEnd"))
                .andExpect(jsonPath("$.date").value(date));
    }

    // CHECKSTYLE-ON: JavadocMethod

}
