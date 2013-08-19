package com.subrosagames.subrosa.domain.account;

/**
 * Provides CRUD functionality for accounts and their subordinate entities.
 */
public interface AccountRepository {

    /**
     * Get the account with the specified id.
     *
     * @param accountId account id
     * @param expansions fields which require expansion
     * @return account optionally populated with expanded fields
     * @throws AccountNotFoundException if account does not exist
     */
    Account getAccount(int accountId, String... expansions) throws AccountNotFoundException;

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
     * Update an existing account with the supplied account information.
     * @param account account
     * @return stored account
     */
    Account update(Account account);

    /**
     * Create an account with the supplied account information.
     *
     * @param account account
     * @param password password
     * @return stored account
     */
    Account create(Account account, String password);

}
