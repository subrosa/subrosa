package com.subrosagames.subrosa.domain.account;

import org.springframework.stereotype.Component;

/**
 * Factory for account objects.
 */
@Component
public class AccountFactory {

    private AccountRepository accountRepository;

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
