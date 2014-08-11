package com.subrosagames.subrosa.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link com.subrosagames.subrosa.api.web.ApiAccountController}.
 */
@TestExecutionListeners({
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiUserControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testRegistrationAndAuthentication() throws Exception {
        Registration registration = new Registration();
        Account account = new Account();
        account.setEmail("jimmy@icanhazemail.com");
        registration.setAccount(account);
        registration.setPassword("password");

        System.out.println(new ObjectMapper().writeValueAsString(registration));
        mockMvc.perform(
                post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jimmy@icanhazemail.com"));

        mockMvc.perform(
                post("/v1/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBuilder().add("email", "jimmy@icanhazemail.com").add("password", "password").build()))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticationWithIncorrectCredentials() throws Exception {
        mockMvc.perform(
                post("/v1/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBuilder().add("email", "jimmy@icanhazemail.com").add("password", "password").build()))
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

//    @Test
    public void testLogout() throws Exception {
    }

    // CHECKSTYLE-ON: JavadocMethod
}
