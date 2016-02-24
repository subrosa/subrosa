package com.subrosagames.subrosa.api.web;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.geo.gmaps.GoogleAddress;
import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;
import com.subrosagames.subrosa.service.AccountService;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;
import static com.subrosagames.subrosa.test.util.SecurityRequestPostProcessors.bearer;

/**
 * Test {@link ApiAccountAddressController}.
 */
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountAddressControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    @InjectMocks
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testListAddresses() throws Exception {
        checkListAddressAssertions(
                mockMvc.perform(get("/account/3/address")
                        .with(bearer(accessTokenForAccountId(3)))));
    }

    @Test
    public void testListAddressesForAuthenticatedUser() throws Exception {
        checkListAddressAssertions(
                mockMvc.perform(get("/user/address")
                        .with(bearer(accessTokenForAccountId(3)))));
    }

    private void checkListAddressAssertions(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(paginatedList()))
                .andExpect(jsonPath("$").value(hasResultCount(2)))
                .andExpect(jsonPath("$.results[*].fullAddress").value(containsInAnyOrder("My Home", "Workplace")));
    }

    @Test
    public void testListAddressesEmpty() throws Exception {
        mockMvc.perform(
                get("/account/1/address")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(paginatedList()))
                .andExpect(jsonPath("$").value(hasResultCount(0)));
    }

    @Test
    public void testListAddressesWrongAccount() throws Exception {
        mockMvc.perform(
                get("/account/3/address")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAddress() throws Exception {
        checkGetAddressAssertions(
                mockMvc.perform(get("/account/3/address/1")
                        .with(bearer(accessTokenForEmail("lotsopics@user.com")))),
                "My Home");
    }

    @Test
    public void testGetAddressForAuthenticatedUser() throws Exception {
        checkGetAddressAssertions(
                mockMvc.perform(get("/user/address/2")
                        .with(bearer(accessTokenForEmail("lotsopics@user.com")))),
                "Workplace");
    }

    private void checkGetAddressAssertions(ResultActions resultActions, String fullAddress) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullAddress").value(fullAddress));
    }

    @Test
    public void testGetAddressWrongAccount() throws Exception {
        mockMvc.perform(
                get("/account/3/address/2")
                        .with(bearer(accessTokenForEmail("bob@user.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/account/3/address/5")
                        .with(bearer(accessTokenForEmail("lotsopics@user.com"))))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testCreateAddress() throws Exception {
        String address = "123 Ivy Ln, Raleigh, NC, US";
        int id = checkNewAddressAssertions(
                mockMvc.perform(post("/account/1/address")
                        .with(bearer(accessTokenForEmail("bob@user.com")))
                        .content(addressJson(address))),
                address);

        checkAddressAssertions(
                mockMvc.perform(get("/user/address/{id}", id)
                        .with(bearer(accessTokenForEmail("bob@user.com")))),
                address);
    }

    @Test
    public void testCreateAddressForAuthenticatedUser() throws Exception {
        String address = "I like games, a, b, c";
        int id = checkNewAddressAssertions(
                mockMvc.perform(post("/user/address")
                        .with(bearer(accessTokenForEmail("bob@user.com")))
                        .content(addressJson(address))),
                address);

        checkAddressAssertions(
                mockMvc.perform(get("/user/address/{id}", id)
                        .with(bearer(accessTokenForEmail("bob@user.com")))),
                address);
    }

    private void checkAddressAssertions(ResultActions resultActions, String fullAddress) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullAddress").value(fullAddress));
    }

    String addressJson(String address, String label) {
        JsonBuilder builder = jsonBuilder().add("fullAddress", address);
        if (label != null) {
            builder.add("label", label);
        }
        return builder.build();
    }

    String addressJson(String address) {
        return addressJson(address, "my label");
    }

    private Integer checkNewAddressAssertions(ResultActions resultActions, String fullAddress) throws Exception {
        String response = resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullAddress").value(fullAddress))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.compile("$.id").read(response);
    }

    @Test
    public void testCreateAddressWithWrongAccountForbidden() throws Exception {
        mockMvc.perform(
                post("/account/1/address")
                        .with(bearer(accessTokenForEmail("lotsopics@user.com")))
                        .content(addressJson("address")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddressLabelRequired() throws Exception {
        Account account = accountRepository.findOne(2);
        String accessToken = accessToken(account);
        mockMvc.perform(post("/user/address")
                .with(bearer(accessToken))
                .content(addressJson("address", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("label", "required"))));
    }

    @Test
    public void testUpdateAddress() throws Exception {
        String address = "street address, city, state, country";
        checkUpdateAddressAssertions(
                mockMvc.perform(put("/account/3/address/2")
                        .with(bearer(accessTokenForEmail("lotsopics@user.com")))
                        .content(addressJson(address))),
                address);

        checkAddressAssertions(
                mockMvc.perform(get("/account/3/address/2")
                        .with(bearer(accessTokenForEmail("lotsopics@user.com")))),
                address);
    }

    @Test
    public void testUpdateAddressForAuthenticatedUser() throws Exception {
        String address = "updated, 1, 2, 3";
        String token = accessToken(accountRepository.findOneByEmail("lotsopics@user.com").get());
        checkUpdateAddressAssertions(
                mockMvc.perform(put("/user/address/2")
                        .with(bearer(token))
                        .content(addressJson(address))),
                address);
        checkAddressAssertions(mockMvc.perform(get("/user/address/2").with(bearer(token))), address);
    }

    private void checkUpdateAddressAssertions(ResultActions resultActions, String fullAddress) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullAddress").value(fullAddress));
    }

    @Test
    public void testUpdateAddressWithCreateResponse() throws Exception {
        String address = "new address, city, state, country";
        String createdAddress = mockMvc.perform(post("/user/address")
                .with(bearer(accessTokenForEmail("bob@user.com")))
                .content(addressJson(address)))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.compile("$.id").read(createdAddress);

        ResultActions resultActions = mockMvc.perform(put("/user/address/{id}", id)
                .with(bearer(accessTokenForEmail("bob@user.com")))
                .content(createdAddress));
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        checkUpdateAddressAssertions(resultActions, address);

        checkAddressAssertions(
                mockMvc.perform(get("/user/address/{id}", id)
                        .with(bearer(accessTokenForEmail("bob@user.com")))),
                address);
    }

    @Test
    public void testDeleteAddress() throws Exception {
        mockMvc.perform(delete("/account/3/address/2")
                .with(bearer(accessTokenForEmail("lotsopics@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        mockMvc.perform(get("/account/3/address/2")
                .with(bearer(accessTokenForEmail("lotsopics@user.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAddressForAuthenticatedUser() throws Exception {
        mockMvc.perform(delete("/user/address/1")
                .with(bearer(accessTokenForEmail("lotsopics@user.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/user/address/1")
                .with(bearer(accessTokenForEmail("lotsopics@user.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAddressNotFound() throws Exception {
        mockMvc.perform(delete("/account/3/address/666")
                .with(bearer(accessTokenForEmail("lotsopics@user.com"))))
                .andExpect(status().isNotFound());
    }

    /**
     * Mock geocoder that simply separates comma-separated fields.
     */
    public static class MockGeocoder extends GoogleGeocoder {

        @Override
        public GoogleAddress geocode(String address) throws IOException {
            GoogleAddress googleAddress = new GoogleAddress();
            String[] strings = address.split(",");
            googleAddress.setFullAddress(address);
            googleAddress.setStreetAddress(StringUtils.trimWhitespace(strings[0]));
            googleAddress.setCity(StringUtils.trimWhitespace(strings[1]));
            googleAddress.setState(StringUtils.trimWhitespace(strings[2]));
            googleAddress.setCountry(StringUtils.trimWhitespace(strings[3]));
            return googleAddress;
        }
    }

    // CHECKSTYLE-ON: JavadocMethod
}
