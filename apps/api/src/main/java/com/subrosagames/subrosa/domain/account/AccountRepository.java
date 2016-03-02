package com.subrosagames.subrosa.domain.account;

import java.util.Optional;

import com.subrosagames.subrosa.domain.DomainObjectRepository;

/**
 * Provides basic CRUD functionality for accounts.
 */
public interface AccountRepository extends DomainObjectRepository<Account, Integer> {

    /**
     * Get the account with the specified email.
     *
     * @param email      email
     * @param expansions fields which require expansion
     * @return account optionally populated with expanded fields
     */
    default Optional<Account> findOneByEmail(String email, String... expansions) {
        return findOne((root, query, cb) -> cb.equal(root.get("email"), email), expansions);
    }

}
