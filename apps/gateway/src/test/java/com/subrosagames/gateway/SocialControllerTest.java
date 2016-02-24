package com.subrosagames.gateway;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.gateway.test.ThirdPartyIntegrations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TODO
 */
public class SocialControllerTest extends AbstractAppTest {

    @Test
    public void testFacebookLogin() throws Exception {
        String clientToken = getClientToken();

        String response = mockMvc.perform(post("/session/facebook")
                .with(bearer(clientToken))
                .content(jsonString(ImmutableMap.of("accessToken", "validToken"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andReturn().getResponse().getContentAsString();
        String accessToken = JsonPath.compile("$.access_token").read(response);

        mockMvc.perform(get("/user")
                .with(bearer(accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(ThirdPartyIntegrations.MockFacebookAdapter.EMAIL));
    }

}