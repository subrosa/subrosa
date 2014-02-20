package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.GameAttributeValue;
import com.subrosagames.subrosa.domain.game.GameHelper;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributeEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributePk;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.ZoneEntity;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.util.QueryHelper;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA-based implementation of the {@link GameRepository}.
 */
@Repository
@Transactional
public class JpaGameRepository implements GameRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JpaGameRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GameEntity create(GameEntity game) throws GameValidationException {
        entityManager.persist(game);
        return game;
    }

    @Override
    public PostEntity create(PostEntity post) {
        entityManager.persist(post);
        return post;
    }

    @Override
    public void save(Lifecycle lifecycle) {
        entityManager.persist(lifecycle);
    }

    @Override
    public GameEntity get(int gameId, String... expansions) throws GameNotFoundException {
        LOG.debug("Retrieving game with id {} from the database", gameId);
        for (String expansion : expansions) {
            ((Session) entityManager.getDelegate()).enableFetchProfile(expansion);
        }
        GameEntity gameEntity = entityManager.find(GameEntity.class, gameId);
        if (gameEntity == null) {
            throw new GameNotFoundException("Game for id " + gameId + " not found");
        }
        return gameEntity;
    }

    @Override
    public GameEntity update(GameEntity gameEntity) throws GameValidationException {
        entityManager.merge(gameEntity);
        return gameEntity;
    }

    @Override
    @Transactional
    public GameEntity get(final String url, String... expansions) throws GameNotFoundException {
        LOG.debug("Retrieving game with url {} from the database", url);
        GameEntity gameEntity;
        try {
            for (String expansion : expansions) {
                LOG.debug("Enabling fetch profile for {}", expansion);
                ((Session) entityManager.getDelegate()).enableFetchProfile(expansion);
            }
            Map<String, Object> conditions = new HashMap<String, Object>(1) {
                {
                    put("url", url);
                }
            };
            TypedQuery<GameEntity> query = QueryHelper.createQuery(entityManager, GameEntity.class, conditions, expansions);
            gameEntity = query.getSingleResult();
        } catch (NoResultException e) {
            throw new GameNotFoundException("Game for url " + url + " not found");
        }
        return gameEntity;
    }

    @Override
    public List<GameEntity> list(int limit, int offset, String... expansions) {
        LOG.debug("Retrieving game list with limit {} and offset {}", limit, offset);
        for (String expansion : expansions) {
            LOG.debug("Enabling fetch profile for {}", expansion);
            ((Session) entityManager.getDelegate()).enableFetchProfile(expansion);
        }
        TypedQuery<GameEntity> query = entityManager.createQuery("SELECT g FROM GameEntity g", GameEntity.class);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.getResultList();
    }

    @Override
    public List<GameEntity> ownedBy(Account user) {
        TypedQuery<GameEntity> query = entityManager.createQuery(
                "SELECT g FROM GameEntity g WHERE g.owner = :owner", GameEntity.class);
        query.setParameter("owner", user);
        return query.getResultList();
    }

    @Override
    public List<Integer> getActiveGames() {
        LOG.debug("Retrieving active games list");
        String jpql = "SELECT ge.id "
                + " FROM GameEntity ge "
                + " WHERE NOW('') > ge.registrationStart "
                + "     AND NOW('') < ge.registrationEnd ";
        TypedQuery<Integer> query = entityManager.createQuery(jpql, Integer.class);
        return query.getResultList();
    }

    @Override
    public List<GameHelper> getGamesNear(Coordinates location) {
        throw new NotImplementedException("Querying for games near a location is not yet supported");
    }

    @Override
    public int count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(g) FROM GameEntity g", Long.class);
        int count = query.getSingleResult().intValue();
        LOG.debug("Queried for game count, found {}", count);
        return count;
    }

    @Override
    public PlayerEntity getPlayerForUserAndGame(int accountId, int gameId) {
        String jpql = "SELECT p "
                + " FROM PlayerEntity p "
                + "     JOIN p.team t "
                + "     JOIN t.game g "
                + " WHERE g.id = :gameId AND p.account.id = :accountId";
        try {
            return entityManager.createQuery(jpql, PlayerEntity.class)
                    .setParameter("gameId", gameId)
                    .setParameter("accountId", accountId)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOG.warn("Attempted to find non-existing player - account {} game {}", accountId, gameId);
            return null;
        }
    }

    @Override
    public List<PlayerEntity> getPlayersForGame(int gameId) {
        String jpql = "SELECT p "
                + " FROM PlayerEntity p "
                + "     JOIN p.team t "
                + "     JOIN t.game g "
                + " WHERE g.id = :gameId";
        return entityManager.createQuery(jpql, PlayerEntity.class)
                .setParameter("gameId", gameId)
                .getResultList();
    }

    @Override
    public void setGameAttribute(GameEntity gameEntity, Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        GameAttributeEntity attributeEntity = entityManager.find(GameAttributeEntity.class, new GameAttributePk(gameEntity.getId(), attributeType.name()));
        if (attributeEntity == null) {
            LOG.debug("Did not find attribute of type {} for game {}. Creating.", attributeType, gameEntity.getId());
            attributeEntity = new GameAttributeEntity(gameEntity, attributeType.name(), attributeValue.name());
        } else {
            LOG.debug("Found attribute of type {} for game {}. Updating.", attributeType, gameEntity.getId());
            attributeEntity.setValue(attributeValue.name());
        }
        entityManager.merge(attributeEntity);
    }

    @Override
    @Transactional
    public List<Zone> getZonesForGame(String gameUrl) throws GameNotFoundException {
        List<Zone> zones = get(gameUrl).getZones();
        LOG.debug("Retrieved {} zones", zones.size());
        return zones;
    }
}
