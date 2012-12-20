package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.token.TokenRepository;
import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 *
 */
@Component
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
