package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.*;

import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.util.QueryHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.security.PasswordUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * JPA-based implementation of CRUD functionality for accounts.
 */
@Repository
@Transactional
public class JpaAccountRepository implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PasswordUtility passwordUtility;

    @Autowired
    private QueryHelper queryExpander;

    @Override
    public Account getAccount(int accountId, String... expansions) throws AccountNotFoundException {
        if (expansions.length > 0) {
            for (String expansion : expansions) {
                ((Session) entityManager.getDelegate()).enableFetchProfile(expansion);
            }
        }
        return entityManager.find(Account.class, accountId);
    }

    @Override
    public Account getAccountByEmail(final String email, String... expansions) throws AccountNotFoundException {
        @SuppressWarnings("serial")
        Map<String, Object> conditions = new HashMap<String, Object>() {{ put("email", email); }};
        TypedQuery<Account> query = queryExpander.createQuery(entityManager, Account.class, conditions, expansions);
        return getSingleResult(query);
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
        try {
            return getAccount(account.getId());
        } catch (AccountNotFoundException e) {
            throw new IllegalStateException("Could not find account right after persisting it?!", e);
        }
    }

    private Account getSingleResult(TypedQuery<Account> query) throws AccountNotFoundException {
        Account account;
        try {
            account = query.getSingleResult();
        } catch (NoResultException e) {
            throw new AccountNotFoundException(e);
        }
        return account;
    }

}
