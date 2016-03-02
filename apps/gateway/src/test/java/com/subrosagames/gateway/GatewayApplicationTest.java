package com.subrosagames.gateway;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountRepository;
import com.subrosagames.gateway.auth.SubrosaPasswordEncoder;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link GatewayApplication}.
 */
public class GatewayApplicationTest extends AbstractAppTest {

    static final String USER_EMAIL = "test@user.com";
    static final String USER_PASSWORD = "testPassword";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UpdateLastLoggedInListener lastLoggedInListener;

    private Account account;

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
    }

    @After
    public void tearDown() throws Exception {
        accountRepository.delete(account);
    }

    @Test
    public void passwordGrant_withoutClientCreds_is401Basic() throws Exception {
        mockMvc.perform(post(OAUTH_TOKEN_URL)
                .param("grant_type", "password"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", startsWith("Bearer realm=")));
    }

    @Test
    public void passwordGrant_withSubrosaClient_badUser_is400InvalidGrant() throws Exception {
        mockMvc.perform(post(OAUTH_TOKEN_URL)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .param("grant_type", "password")
                .param("username", "non.existent@user.com")
                .param("password", "irrelevant"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    @Test
    public void passwordGrant_subrosaClient_goodPassword_is200() throws Exception {
        mockMvc.perform(post(OAUTH_TOKEN_URL)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .param("grant_type", "password")
                .param("username", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.expires_in").exists())
                .andExpect(jsonPath("$.scope").exists())
                .andExpect(jsonPath("$.token_type").value("bearer"));
    }

    @Test
    public void passwordGrant_successful_updatesLastLoggedIn() throws Exception {
        Instant instant = Instant.now();
        lastLoggedInListener.setClock(Clock.fixed(instant, ZoneOffset.UTC));

        mockMvc.perform(post(OAUTH_TOKEN_URL)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .param("grant_type", "password")
                .param("username", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andExpect(status().isOk());
        Account account = accountRepository.findOneByEmail(this.account.getEmail()).get();
        assertEquals(instant.toEpochMilli(), account.getLastLoggedIn().getTime());

        lastLoggedInListener.setClock(Clock.systemUTC());
    }

}