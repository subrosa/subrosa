package com.subrosagames.subrosa.api.web;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.test.util.ForeignKeyDisablingTestListener;
import com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.userDetailsService;

/**
 * Base class for MVC tests.
 * <p/>
 * Provides scaffolding for performing mock requests, along with helpers for generating domain objects.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "/test-context.xml" })
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        ForeignKeyDisablingTestListener.class
})
public abstract class AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocType

    /**
     * Provides mechanism for performing and verifying API calls.
     */
    protected MockMvc mockMvc; // SUPPRESS CHECKSTYLE VisibilityModifier

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .defaultRequest(get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .build();
    }

    protected SecurityRequestPostProcessors.UserDetailsRequestPostProcessor user(String email) {
        return userDetailsService(email).userDetailsServiceBeanId("userDetailsService");
    }

    protected long timeDaysInFuture(int days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTimeInMillis();
    }

    protected String newRandomUser() throws Exception {
        String rnd = RandomStringUtils.randomAlphanumeric(10);
        JsonBuilder account = jsonBuilder().add("email", rnd + "@gmail.com");
        String registration = jsonBuilder().addChild("account", account).add("password", rnd).build();
        System.out.println(registration);
        MvcResult result = mockMvc.perform(
                post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.compile("$.email").read(result.getResponse().getContentAsString());
    }

    protected JsonBuilder jsonBuilder() {
        return new JsonBuilder();
    }

    protected String newRandomGame(GameType gameType) throws Exception {
        String email = newRandomUser();
        MvcResult result = mockMvc.perform(
                post("/game")
                        .with(user(email))
                        .content(jsonBuilder()
                                .add("name", RandomStringUtils.random(10))
                                .add("gameType", gameType.name()).build()))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.compile("$.url").read(result.getResponse().getContentAsString());
    }

    protected ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
        return resultActions;
    }

    protected class JsonBuilder {
        private final JsonMap jsonMap;

        public JsonBuilder() {
            jsonMap = new JsonMap();
        }

        public JsonBuilder add(String key, Object value) {
            jsonMap.put(key, value);
            return this;
        }

        public JsonBuilder addChild(String key, JsonBuilder child) {
            jsonMap.put(key, child.jsonMap);
            return this;
        }

        public String build() {
            return jsonMap.toString();
        }
    }

    protected class JsonMap extends HashMap<String, Object> {
        public String toString() {
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    // CHECKSTYLE-ON: JavadocMethod
    // CHECKSTYLE-ON: JavadocType
}
