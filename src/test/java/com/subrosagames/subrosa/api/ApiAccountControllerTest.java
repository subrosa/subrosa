package com.subrosagames.subrosa.api;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link ApiAccountController}.
 */
@TestExecutionListeners({
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountControllerTest extends AbstractApiControllerTest {

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
    public void testOwnedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/10000")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
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
    public void testNonexistantAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/934834")
                        .with(user("joe@admin.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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
                .andExpect(status().isNotFound());
    }

}
