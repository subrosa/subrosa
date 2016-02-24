package com.subrosagames.gateway;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.userDetailsService;

/**
 * TODO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = GatewayApplication.class)
@ComponentScan(basePackages = "com.subrosagames.gateway.test")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
})
@ActiveProfiles("unit-test")
public abstract class AbstractAppTest {

    static final String OAUTH_TOKEN_URL = "/oauth/token";
    static final String CLIENT_SECRET = "subrosa";
    static final String CLIENT_ID = "subrosa";

    protected MockMvc mockMvc;

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(filterChainProxy)
                .defaultRequest(get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    protected String jsonString(Object value) throws JsonProcessingException {
        return new ObjectMapper().writer().writeValueAsString(value);
    }

    protected String getClientToken() throws Exception {
        String tokenResponse = mockMvc.perform(post(OAUTH_TOKEN_URL)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .param("grant_type", "client_credentials"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(tokenResponse);
        return JsonPath.compile("$.access_token").read(tokenResponse);
    }

    protected RequestPostProcessor user(String email) {
        return userDetailsService(email).userDetailsServiceBeanId("userDetailsService");
    }

    protected RequestPostProcessor httpBasic(String testclient, String testsecret) {
        String auth = Base64Utils.encodeToString((testclient + ":" + testsecret).getBytes());
        return request -> {
            request.addHeader("Authorization", "Basic " + auth);
            return request;
        };
    }

    protected RequestPostProcessor bearer(String token) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }
}
