package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.security.PasswordUtility;

/**
 * JPA-based implementation of CRUD functionality for accounts.
 */
@Repository
@Transactional
public class JpaAccountRepository implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordUtility passwordUtility;

    @Override
    public Account getAccount(int accountId) {
        return entityManager.find(Account.class, accountId);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return entityManager.createQuery("SELECT a FROM Account a WHERE a.email = :email", Account.class)
                .setParameter("email", email).getResultList().get(0);
    }

    @Override
    public Account update(Account account) {
        entityManager.merge(account);
        return account;
    }

    @Override
    public Account create(Account account, String password) {
        account.setPassword(passwordUtility.encryptPassword(password));
        entityManager.persist(account);
        return getAccount(account.getId());
    }

}
