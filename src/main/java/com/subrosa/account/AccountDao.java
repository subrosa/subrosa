package com.subrosa.account;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides CRUD functionality for accounts.
 */
@Repository
public class AccountDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Account getAccount(int accountId) {
        return entityManager.find(Account.class, accountId);
    }

    public Account getAccountByEmail(String email) {
        return entityManager.createQuery("SELECT a FROM Account a WHERE a.email = :email", Account.class)
                .setParameter("email", email).getResultList().get(0);
    }
}
