package com.subrosagames.subrosa.api;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;

import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link ApiAccountController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
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
    public void testListAccountsNonAdmin() throws Exception {
        mockMvc.perform(
                get("/account")
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListAccountsAdmin() throws Exception {
        mockMvc.perform(
                get("/account")
                        .with(user("joe@admin.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())));
    }

}
