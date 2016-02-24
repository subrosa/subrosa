package com.subrosagames.gateway;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jayway.jsonpath.JsonPath;
import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountRepository;
import com.subrosagames.gateway.auth.SubrosaPasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link UserController}.
 */
public class UserControllerTest extends AbstractAppTest {

    static final String USER_EMAIL = "test@user.com";
    static final String USER_PASSWORD = "testPassword";

    @Autowired
    private AccountRepository accountRepository;

    private Account account;
    private String accessToken;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        account = Account.builder()
                .accountRepository(accountRepository)
                .passwordEncoder(new SubrosaPasswordEncoder())
                .id(1)
                .email(USER_EMAIL)
                .build().create(USER_PASSWORD);
        String tokenResponse = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .param("grant_type", "password")
                .param("username", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        accessToken = JsonPath.compile("$.access_token").read(tokenResponse);
    }

    @After
    public void tearDown() throws Exception {
        accountRepository.delete(account);
    }

    @Test
    public void user() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(account.getEmail()));
    }

    @Test
    public void user_withInvalidAccessToken_is401_invalid_token() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer this-is-bad-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_token"));
    }

    @Test
    public void user_withoutAccessToken_is401_unauthorized() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    @Test
    public void revoke() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(account.getEmail()));

        mockMvc.perform(post("/oauth/revoke")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void revoke_withInvalidToken_is401() throws Exception {
        mockMvc.perform(post("/oauth/revoke")
                .header("Authorization", "Bearer this-is-bad-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_token"));
    }
}