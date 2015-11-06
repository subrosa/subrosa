package com.subrosagames.subrosa.service;

import java.util.Optional;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.subrosagames.subrosa.api.dto.AddressDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.test.AbstractContextTest;

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test {@link AccountService}.
 */
public class AccountServiceTest extends AbstractContextTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private Account account;

    @Before
    public void setUp() throws Exception {
        account = accountRepository.save(Account.builder()
                .email("email" + new Random().nextInt() + "@example.com")
                .password("password")
                .build());
        login(account.getId());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

    @Test
    public void createAddress() throws Exception {
        Address address = accountService.createAddress(account.getId(), AddressDescriptor.builder()
                .label(Optional.of("home"))
                .fullAddress(Optional.of("123 Test Ave, Raleigh, NC 27601"))
                .build());
        assertEquals("home", address.getLabel());
        address = accountService.getAddress(account.getId(), address.getId());
        assertEquals("home", address.getLabel());
    }

    @Test
    public void testDeleteAddress() throws Exception {
        Address address = accountService.createAddress(account.getId(), AddressDescriptor.builder()
                .label(Optional.of("home"))
                .fullAddress(Optional.of("123 Test Ave, Raleigh, NC 27601"))
                .build());
        Address deleted = accountService.deleteAddress(account.getId(), address.getId());
        assertNotNull(deleted);
        assertThatThrownBy(() -> accountService.getAddress(account.getId(), deleted.getId()))
                .isExactlyInstanceOf(AddressNotFoundException.class);
        assertThatThrownBy(() -> accountService.deleteAddress(account.getId(), deleted.getId()))
                .isExactlyInstanceOf(AddressNotFoundException.class);
    }
}