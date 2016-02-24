package com.subrosagames.subrosa.api.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.AccountRole;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.test.AbstractContextTest;
import com.subrosagames.subrosa.test.util.ColumnSensingFlatXmlDataSetLoader;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.bearer;

/**
 * Base class for MVC tests.
 * <p>
 * Provides scaffolding for performing mock requests, along with helpers for generating domain objects.
 */
@DbUnitConfiguration(dataSetLoader = ColumnSensingFlatXmlDataSetLoader.class)
@TestExecutionListeners({
        WithSecurityContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
})
public abstract class AbstractApiControllerTest extends AbstractContextTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocType

    /**
     * Provides mechanism for performing and verifying API calls.
     */
    protected MockMvc mockMvc; // SUPPRESS CHECKSTYLE VisibilityModifier

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Qualifier("inMemoryTokenStore")
    private TokenStore tokenStore;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    protected MockHttpSession user(String email) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                Account account = accountRepository.findOneByEmail(email).get();
                return new PreAuthenticatedAuthenticationToken(account, "",
                        account.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toList()));
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }
        });
        return session;
    }

    protected String accessTokenForAccountId(int id) {
        Account account = accountRepository.findOne(id);
        return accessToken(account);
    }

    protected String accessTokenForEmail(String email) {
        Account account = accountRepository.findOneByEmail(email).get();
        return accessToken(account);
    }

    protected String accessToken(Account account) {
        Collection<? extends GrantedAuthority> authorities = account.grantedAuthorities();
        OAuth2Request oAuth2Request = new OAuth2Request(
                new HashMap<>(), "subrosa", authorities, true, new HashSet<>(), new HashSet<>(), "", null, null
        );
        String token = UUID.randomUUID().toString();
        Authentication userAuthentication = new PreAuthenticatedAuthenticationToken(new SubrosaUser(account), token, authorities);
        OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
        tokenStore.storeAccessToken(new DefaultOAuth2AccessToken(token), authentication);
        return token;
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

    protected String newRandomGame(GameType gameType, String userEmail) throws Exception {
        MvcResult result = mockMvc.perform(
                post("/game")
                        .with(bearer(accessTokenForEmail(userEmail)))
                        .content(jsonBuilder()
                                .add("name", RandomStringUtils.random(10))
                                .add("gameType", gameType.name()).build()))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.compile("$.url").read(result.getResponse().getContentAsString());
    }

    protected String newRandomGame(GameType gameType) throws Exception {
        String email = newRandomUser();
        return newRandomGame(gameType, email);
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
