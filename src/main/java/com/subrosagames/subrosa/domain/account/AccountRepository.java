package com.subrosagames.subrosa.domain.account;

/**
 * Provides CRUD functionality for accounts and their subordinate entities.
 */
public interface AccountRepository {

    /**
     * Get the account with the specified id.
     * @param accountId account id
     * @return populated account if found, {@code null} otherwise.
     */
    Account getAccount(int accountId);

    /**
     * Get the account with the specified email.
     * @param email email
     * @return populated account if found, {@code null} otherwise.
     */
    Account getAccountByEmail(String email);

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
