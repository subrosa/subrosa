package com.subrosagames.gateway.auth;

import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.subrosagames.subrosa.security.ConnectionProvider;

/**
 * JPA-based repository for user connections.
 */
@Repository
public class SocialUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find user connections for given provider name provider's user id.
     *
     * @param provider       provider name
     * @param providerUserId provider's user id
     * @return list of user connections
     */
    public List<UserConnection> findByProviderAndProviderUserId(String provider, String providerUserId) {
        return entityManager.createQuery("select c from UserConnection c where provider = :provider and providerUserId = :providerUserId", UserConnection.class)
                .setParameter("provider", ConnectionProvider.forProviderName(provider))
                .setParameter("providerUserId", providerUserId)
                .getResultList();
    }

    /**
     * Find user ids for given provider name and provider's user id.
     *
     * @param provider       provider name
     * @param providerUserId provider's user id
     * @return list of user ids
     */
    public List<String> userIdsByProviderAndProviderUserId(String provider, Set<String> providerUserId) {
        return entityManager.createQuery("select s.id from SocialUser s where provider = :provider and providerUserId = :providerUserId", String.class)
                .setParameter("provider", ConnectionProvider.forProviderName(provider))
                .setParameter("providerUserId", providerUserId)
                .getResultList();
    }

    /**
     * Persist the provided user connection.
     *
     * @param userConnection user connection
     */
    public void create(UserConnection userConnection) {
        entityManager.persist(userConnection);
    }
}
