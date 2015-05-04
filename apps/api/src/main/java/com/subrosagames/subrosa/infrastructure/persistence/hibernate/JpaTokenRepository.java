package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.token.TokenRepository;
import com.subrosagames.subrosa.domain.token.TokenType;
import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 * Manages storage and retrieval of generated tokens.
 */
@Repository
@Transactional
public class JpaTokenRepository implements TokenRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TokenEntity findToken(String token, TokenType tokenType) {
        try {
            return entityManager.createQuery("SELECT t FROM TokenEntity t WHERE t.token = :token AND t.tokenType = :tokenType", TokenEntity.class)
                    .setParameter("token", token)
                    .setParameter("tokenType", tokenType)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void storeToken(TokenEntity tokenEntity) {
        entityManager.persist(tokenEntity);
    }

    @Override
    public void deleteTokensForAccount(Account account) {
        entityManager.createQuery("DELETE FROM TokenEntity t WHERE t.accountId = :accountId")
                .setParameter("accountId", account.getId())
                .executeUpdate();
    }
}
