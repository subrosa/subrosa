package com.subrosagames.subrosa.api;

import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link com.subrosagames.subrosa.api.ApiAccountController}.
 */
@TestExecutionListeners({
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiUserControllerTest extends AbstractApiControllerTest {

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

    @Test
    public void testCurrentUser() throws Exception {
        mockMvc.perform(
                get("/user")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testUnauthenticatedCurrentUser() throws Exception {
        mockMvc.perform(
                get("/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

}