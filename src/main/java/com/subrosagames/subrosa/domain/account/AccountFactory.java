package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.service.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for account objects.
 */
@Component
public class AccountFactory {

    @Autowired
    private AccountRepository accountRepository;

    public PaginatedList<Account> getAccounts(Integer limit, Integer offset, String... expansions) {
        List<Account> accounts = accountRepository.list(limit, offset, expansions);
        return new PaginatedList<Account>(
                accounts,
                accountRepository.count(),
                limit, offset);
    }
}
