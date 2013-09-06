package com.subrosagames.subrosa.api;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.userDetailsService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link ApiAccountController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "/test-context.xml" })
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void testUnauthenticatedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/10000"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUnauthorizedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/10001")
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAdminAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/10000")
                        .with(user("joe@admin.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testOwnedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/10000")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testRegistrationAndAuthentication() throws Exception {
        Registration registration = new Registration();
        Account account = new Account();
        account.setEmail("jimmy@icanhazemail.com");
        registration.setAccount(account);
        registration.setPassword("password");

        mockMvc.perform(
                post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jimmy@icanhazemail.com"));

        mockMvc.perform(
                post("/v1/authenticate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "jimmy@icanhazemail.com")
                        .param("password", "password"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticationWithIncorrectCredentials() throws Exception {
        mockMvc.perform(
                post("/v1/authenticate")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("email", "random@email.org")
                    .param("password", "incorrect"))
                .andExpect(status().isUnauthorized());
    }

    private SecurityRequestPostProcessors.UserDetailsRequestPostProcessor user(String email) {
        return userDetailsService(email).userDetailsServiceBeanId("userDetailsService");
    }

}
