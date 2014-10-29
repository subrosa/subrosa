package com.subrosagames.subrosa.api.web;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenType;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link com.subrosagames.subrosa.api.web.ApiAccountController}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountControllerTest.class);

    @Autowired
    private TokenFactory tokenFactory;

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
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testOwnedAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/1")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testAdminAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/1")
                        .with(user("joe@admin.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@user.com"));
    }

    @Test
    public void testAccountRetrievalIncludesRoles() throws Exception {
        mockMvc.perform(
                get("/account/1000")
                        .with(user("joe@admin.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joe@admin.com"))
                .andExpect(jsonPath("$.roles").exists())
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    public void testNonexistantAccountRetrieval() throws Exception {
        mockMvc.perform(
                get("/account/934834")
                        .with(user("joe@admin.com")))
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

    @Test
    public void testCreateAccountMissingFields() throws Exception {
        Set<String> requests = new HashSet<String>() {
            {
                add("");
                add(jsonBuilder().add("password", "mysecret").build());
                add(jsonBuilder().add("password", "mysecret").add("account", null).build());
                add(jsonBuilder().add("password", "mysecret").addChild("account", jsonBuilder()).build());
                add(jsonBuilder().add("password", "mysecret").addChild("account",
                        jsonBuilder().add("email", "")).build());
                add(jsonBuilder().add("password", "mysecret").addChild("account",
                        jsonBuilder().add("email", "bademail")).build());
                add(jsonBuilder().add("password", null).addChild("account",
                        jsonBuilder().add("email", "good@email.com")).build());
                add(jsonBuilder().add("password", "").addChild("account",
                        jsonBuilder().add("email", "good@email.com")).build());
            }
        };
        for (String request : requests) {
            assertIsBadRequestCreatingAccount(request);
        }
    }

    private void assertIsBadRequestCreatingAccount(String content) throws Exception {
        LOG.debug("Asserting bad request creating account: {}", content);
        mockMvc.perform(
                post("/account")
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testCreateAccount() throws Exception {
        mockMvc.perform(
                post("/account")
                        .content(jsonBuilder()
                                .add("password", "mysecret")
                                .addChild("account", jsonBuilder()
                                        .add("email", "new@user.com"))
                                .build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@user.com"));
    }

    @Test
    public void testCreateAccountDuplicateEmail() throws Exception {
        mockMvc.perform(
                post("/account")
                        .content(jsonBuilder()
                                .add("password", "mysecret")
                                .addChild("account", jsonBuilder()
                                        .add("email", "bob@user.com"))
                                .build()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("email", "unique"))));
    }

    @Test
    public void testUpdateAccountNoChanges() throws Exception {
        String response = mockMvc.perform(
                get("/account/1")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                put("/account/1")
                        .with(user("bob@user.com"))
                        .content(response))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAccount() throws Exception {
        String newName = "Johnny Hotcakes testUpdateAccount";
        mockMvc.perform(
                put("/account/1")
                        .with(user("bob@user.com"))
                        .content(jsonBuilder().add("name", newName).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));

        mockMvc.perform(
                get("/account/1")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    public void testUpdateEmailSetsActivatedFalse() throws Exception {
        mockMvc.perform(
                get("/account/1")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activated").value(true));

        mockMvc.perform(
                put("/account/1")
                        .with(user("bob@user.com"))
                        .content(jsonBuilder().add("email", "new@email.com").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activated").value(false));

        mockMvc.perform(
                get("/account/1")
                        .with(user("new@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activated").value(false));
    }

    @Test
    public void testActivateAccount() throws Exception {
        mockMvc.perform(
                get("/account/2")
                        .with(user("notactive@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activated").value(false));

        mockMvc.perform(
                post("/account/2/activate")
                        .with(user("notactive@user.com")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                post("/account/2/activate")
                        .with(user("notactive@user.com"))
                        .content(jsonBuilder().add("token", "wrong").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("token", "invalid"))));

        String newToken = tokenFactory.generateNewToken(2, TokenType.EMAIL_VALIDATION);

        mockMvc.perform(
                post("/account/2/activate")
                        .with(user("notactive@user.com"))
                        .content(jsonBuilder().add("token", newToken).build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("notactive@user.com"))
                .andExpect(jsonPath("$.activated").value(true));
    }

    // CHECKSTYLE-ON: JavadocMethod

}
