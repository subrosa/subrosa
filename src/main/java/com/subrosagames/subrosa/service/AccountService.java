package com.subrosagames.subrosa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;

/**
 * Service layer for account operations.
 */
@Service
public class AccountService {

    @Autowired
    private AccountFactory accountFactory;

    /**
     * Get specified account.
     *
     * @param accountId account id
     * @param expand    field expansions
     * @return account
     * @throws AccountNotFoundException if account does not exist
     */
    @PreAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public Account getAccount(Integer accountId, String... expand) throws AccountNotFoundException {
        return accountFactory.getAccount(accountId, expand);
    }
}
