package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.subrosagames.subrosa.domain.token.TokenRepository;
import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages storage and retrieval of generated tokens.
 */
@Repository
@Transactional
public class JpaTokenRepository implements TokenRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TokenEntity findToken(String token) {
        return entityManager.createQuery("SELECT t FROM TokenEntity t WHERE t.token = :token", TokenEntity.class)
                .setParameter("token", token)
                .getSingleResult();
    }

    @Override
    public void storeToken(TokenEntity tokenEntity) {
        entityManager.persist(tokenEntity);
    }
}
