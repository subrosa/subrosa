package com.subrosagames.subrosa.api.web;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.bearer;

/**
 * Test {@link com.subrosagames.subrosa.api.web.ApiAccountController}.
 */
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testUnauthenticatedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUnauthorizedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/1000")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testOwnedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/1")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testAdminAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/1")
                        .with(bearer(accessTokenForEmail("joe@admin.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testAccountRetrievalIncludesRoles() throws Exception {
        mockMvc.perform(
                get("/account/1000")
                        .with(bearer(accessTokenForEmail("joe@admin.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joe@admin.com"))
                .andExpect(jsonPath("$.roles").exists())
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    public void testNonexistantAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/934834")
                        .with(bearer(accessTokenForEmail("joe@admin.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testListAccountsNonAdmin() throws Exception {
        mockMvc.perform(
                get("/account")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListAccountsAdmin() throws Exception {
        mockMvc.perform(
                get("/account")
                        .with(bearer(accessTokenForEmail("joe@admin.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())));
    }

    @Test
    public void testUpdateAccountNoChanges() throws Exception {
        String response = mockMvc.perform(
                get("/account/1")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                put("/account/1")
                        .with(bearer(accessTokenForEmail("bob@user.com")))
                        .content(response))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAccount() throws Exception {
        String newName = "Johnny Hotcakes testUpdateAccount";
        mockMvc.perform(
                put("/account/1")
                        .with(bearer(accessTokenForEmail("bob@user.com")))
                        .content(jsonBuilder().add("name", newName).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));

        mockMvc.perform(
                get("/account/1")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    // CHECKSTYLE-ON: JavadocMethod

}
