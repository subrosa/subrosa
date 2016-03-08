package com.subrosagames.gateway;

import org.junit.Before;
import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link ClientController}.
 */
public class ClientControllerTest extends AbstractAppTest {

    private String accessToken;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String tokenResponse = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .param("grant_type", "client_credentials"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        accessToken = JsonPath.compile("$.access_token").read(tokenResponse);
    }

    @Test
    public void client() throws Exception {
        mockMvc.perform(get("/client")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(CLIENT_ID));
    }

}