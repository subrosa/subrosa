package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.UnknownProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.subrosa.api.actions.list.QueryBuilder;
import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.util.QueryHelper;
import com.subrosagames.subrosa.security.PasswordUtility;

/**
 * JPA-based implementation of CRUD functionality for accounts.
 */
@Repository
@Transactional
public class JpaAccountRepository implements AccountRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JpaAccountRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordUtility passwordUtility;

    @Override
    @PostAuthorize("hasPermission(returnObject, 'VIEW_ACCOUNT')")
    public Account get(int accountId, String... expansions) throws AccountNotFoundException {
        return getUnauthenticated(accountId, expansions);
    }

    public Account getUnauthenticated(int accountId, String... expansions) throws AccountNotFoundException {
        expansions = enableExpansions(expansions);
        Account account = entityManager.find(Account.class, accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account with id " + accountId + " not found");
        }
        return account;
    }

    @Override
    public List<Address> addressesWhere(Map<String, Object> conditions) {
        QueryCriteria<Address> criteria = new QueryCriteria<Address>(Address.class);
        criteria.setBypassFilterableChecks(true);
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            criteria.addFilter(entry.getKey(), entry.getValue());
        }
        QueryBuilder<Address, TypedQuery<Address>, TypedQuery<Long>> queryBuilder = new JpaQueryBuilder<Address>(entityManager);
        TypedQuery<Address> query = queryBuilder.getQuery(criteria);
        return query.getResultList();
    }

    @Override
    public Address update(Address address) {
        return entityManager.merge(address);
    }

    @Override
    public Account getAccountByEmail(final String email, String... expansions) throws AccountNotFoundException {
        @SuppressWarnings("serial")
        Map<String, Object> conditions = new HashMap<String, Object>() {
            {
                put("email", email);
            }
        };
        TypedQuery<Account> query = QueryHelper.createQuery(entityManager, Account.class, conditions, expansions);
        return getSingleResult(query);
    }

    @Override
    public Account update(Account account) throws AccountNotFoundException {
        entityManager.merge(account);
        return account;
    }

    @Override
    public Account create(Account account, String password) throws AccountValidationException {
        account.setPassword(passwordUtility.encryptPassword(password));
        return create(account);
    }

    @Override
    public Account create(Account account) throws AccountValidationException {
        if (account.getPassword() == null) {
            throw new AccountValidationException("Cannot create an account without a password");
        }
        entityManager.persist(account);
        try {
            return get(account.getId());
        } catch (AccountNotFoundException e) {
            throw new IllegalStateException("Could not find account right after persisting it?!", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> list(int limit, int offset, String... expansions) {
        return null;
    }

    @Override
    public int count() {
        return 0;
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

    private String[] enableExpansions(String... expansions) {
        List<String> enabled = new ArrayList<String>(expansions.length);
        for (String expansion : expansions) {
            try {
                ((Session) entityManager.getDelegate()).enableFetchProfile(expansion);
                enabled.add(expansion);
            } catch (UnknownProfileException e) {
                LOG.error("Bad fetch profile " + e.getName() + " requested. Swallowing exception and removing profile.", e);
            }
        }
        return enabled.toArray(new String[enabled.size()]);
    }

}
