package com.subrosagames.subrosa.api.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.test.AbstractContextTest;
import com.subrosagames.subrosa.test.util.ColumnSensingFlatXmlDataSetLoader;
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
@DbUnitConfiguration(dataSetLoader = ColumnSensingFlatXmlDataSetLoader.class)
@TestExecutionListeners(DbUnitTestExecutionListener.class)
public abstract class AbstractApiControllerTest extends AbstractContextTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocType

    /**
     * Provides mechanism for performing and verifying API calls.
     */
    protected MockMvc mockMvc; // SUPPRESS CHECKSTYLE VisibilityModifier

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(filterChainProxy)
                .defaultRequest(get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .alwaysDo(MockMvcResultHandlers.print())
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

    protected static class JsonBuilder {
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

        public JsonBuilder addArray(String key, final JsonBuilder... jsonBuilders) {
            Collection<Map<String, Object>> jsonArray = Collections2.transform(Arrays.asList(jsonBuilders), new Function<JsonBuilder, Map<String, Object>>() {
                @Nullable
                @Override
                public Map<String, Object> apply(@Nullable JsonBuilder input) {
                    return input != null ? input.jsonMap : null;
                }
            });
            jsonMap.put(key, jsonArray);
            return this;
        }
    }

    protected static class JsonMap extends HashMap<String, Object> {
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
