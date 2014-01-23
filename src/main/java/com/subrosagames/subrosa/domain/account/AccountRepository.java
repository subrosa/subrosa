package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.domain.DomainRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Provides CRUD functionality for accounts and their subordinate entities.
 */
public interface AccountRepository extends DomainRepository<Account> {

    /**
     * Get the account with the specified email.
     *
     * @param email email
     * @param expansions fields which require expansion
     * @return account optionally populated with expanded fields
     * @throws AccountNotFoundException if account does not exist
     */
    Account getAccountByEmail(String email, String... expansions) throws AccountNotFoundException;

    Account get(int id, String... expansions) throws AccountNotFoundException;

    /**
     * Create an account with the given password.
     *
     * @param account account
     * @param password password
     * @return created account
     * @throws AccountValidationException if account fails validation
     */
    Account create(Account account, String password) throws AccountValidationException;

    Account create(Account account) throws AccountValidationException;
    Account update(Account account) throws AccountNotFoundException, AccountValidationException;

    Account getUnauthenticated(int id) throws AccountNotFoundException;
}
