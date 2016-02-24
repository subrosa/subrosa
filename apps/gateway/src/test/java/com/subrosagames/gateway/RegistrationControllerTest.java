package com.subrosagames.gateway;

import java.util.Optional;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;
import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountRepository;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;


/**
 * TODO
 */
public class RegistrationControllerTest extends AbstractAppTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void register_withoutClient_is401Basic() throws Exception {
        String email = randomEmail();
        mockMvc.perform(post("/account")
                .content(jsonString(ImmutableMap.of("email", email, "password", "password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", startsWith("Bearer realm=")));
    }

    @Test
    public void register_withEmailAndPassword_succeeds() throws Exception {
        String token = getClientToken();
        String email = randomEmail();
        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("email", email, "password", "password"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));

        Optional<Account> optional = accountRepository.findOneByEmail(email);
        assertTrue(optional.isPresent());
        Account account = optional.get();
        assertEquals(email, account.getEmail());
        assertNotNull(account.getPassword());
        assertNull(account.getPhone());
        assertFalse(account.getActivated());

        accountRepository.delete(account);
    }

    @Test
    public void register_withoutPassword_is400BadRequest() throws Exception {
        String token = getClientToken();
        String email = randomEmail();
        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("email", email))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("password", "required"))));
    }

    @Test
    public void register_withPhoneAndPassword_succeeds() throws Exception {
        String token = getClientToken();
        String phone = randomPhone();
        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("phone", phone, "password", "password"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").value(phone));

        Optional<Account> optional = accountRepository.findOneByPhone(phone);
        assertTrue(optional.isPresent());
        Account account = optional.get();
        assertEquals(phone, account.getPhone());
        assertNotNull(account.getPassword());
        assertNull(account.getEmail());
        assertFalse(account.getActivated());

        accountRepository.delete(account);
    }

    @Test
    public void register_withNonUniqueEmail_is409Conflict() throws Exception {
        String token = getClientToken();
        String email = randomEmail();
        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("email", email, "password", "password"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("email", email, "password", "password"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("email", "unique"))));

        accountRepository.delete(accountRepository.findOneByEmail(email).get());
    }

    @Test
    public void register_withNonUniquePhone_is409Conflict() throws Exception {
        String token = getClientToken();
        String phone = randomPhone();
        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("phone", phone, "password", "password"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").value(phone));
        mockMvc.perform(post("/account")
                .with(bearer(token))
                .content(jsonString(ImmutableMap.of("phone", phone, "password", "password"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("phone", "unique"))));

        accountRepository.delete(accountRepository.findOneByPhone(phone).get());
    }

    @Test
    public void activate_withToken_activatesAccount() throws Exception {

    }

    @Test
    public void update_withNewEmail_deactivatesAccount() throws Exception {

    }

    @Test
    public void update_withSameEmail_accountRemainsActivated() throws Exception {

    }

    @Test
    public void update_withNonUniqueEmail_is409Conflict() throws Exception {

    }

    private String randomEmail() {
        return RandomStringUtils.randomAlphanumeric(10) + "@icanhazemail.com";
    }

    private String randomPhone() {
        return RandomStringUtils.randomNumeric(10);
    }

}