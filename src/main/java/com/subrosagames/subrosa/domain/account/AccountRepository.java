package com.subrosagames.subrosa.domain.account;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.domain.DomainRepository;

/**
 * Provides CRUD functionality for accounts and their subordinate entities.
 */
public interface AccountRepository extends DomainRepository<Account, Account> {

    /**
     * Get the account with the specified email.
     *
     * @param email email
     * @param expansions fields which require expansion
     * @return account optionally populated with expanded fields
     * @throws AccountNotFoundException if account does not exist
     */
    Account getAccountByEmail(String email, String... expansions) throws AccountNotFoundException;

    /**
     * Get account with the specified id.
     *
     * @param id object id
     * @param expansions fields to expand
     * @return account
     * @throws AccountNotFoundException if account with specified id does not exist
     */
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

    /**
     * Save the given account to the database.
     *
     * @param account account to save
     * @return saved account
     * @throws AccountNotFoundException if no account row exists for given account
     * @throws AccountValidationException if account is invalid for update
     */
    Account update(Account account) throws AccountNotFoundException, AccountValidationException;

    Account getUnauthenticated(int id) throws AccountNotFoundException;

    List<Address> addressesWhere(Map<String, Object> conditions);

    Address update(Address address);
}
