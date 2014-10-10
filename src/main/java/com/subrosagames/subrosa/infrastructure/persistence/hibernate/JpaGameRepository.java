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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.subrosa.api.actions.list.QueryBuilder;
import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.GameAttributeValue;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributeEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributePk;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.util.QueryHelper;

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
    public BaseGame create(BaseGame game) throws GameValidationException {
        entityManager.persist(game);
        return game;
    }

    @Override
    public PostEntity create(PostEntity post) {
        entityManager.persist(post);
        return post;
    }

    @Override
    public LocationEntity create(LocationEntity location) {
        entityManager.persist(location);
        return location;
    }

    @Override
    public BaseGame get(int gameId, String... expansions) throws GameNotFoundException {
        LOG.debug("Retrieving game with id {} from the database", gameId);
        expansions = enableExpansions(expansions);
        BaseGame gameEntity = entityManager.find(BaseGame.class, gameId);
        if (gameEntity == null) {
            throw new GameNotFoundException("Game for id " + gameId + " not found");
        }
        return gameEntity;
    }

    @Override
    public BaseGame update(BaseGame game) throws GameValidationException {
        return entityManager.merge(game);
    }

    @Override
    public BaseGame get(final String url, String... expansions) throws GameNotFoundException {
        LOG.debug("Retrieving game with url {} from the database", url);
        BaseGame gameEntity;
        expansions = enableExpansions(expansions);
        Map<String, Object> conditions = new HashMap<String, Object>(1) {
            {
                put("url", url);
            }
        };
        TypedQuery<BaseGame> query = QueryHelper.createQuery(entityManager, BaseGame.class, conditions, expansions);
        try {
            gameEntity = query.getSingleResult();
        } catch (NoResultException e) {
            throw new GameNotFoundException("Game for url " + url + " not found");
        }
        return gameEntity;
    }

    @Override
    public List<BaseGame> list(int limit, int offset, String... expansions) {
        LOG.debug("Retrieving game list with limit {} and offset {}", limit, offset);
        expansions = enableExpansions(expansions);
        TypedQuery<BaseGame> query = entityManager.createQuery("SELECT g FROM BaseGame g", BaseGame.class);
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        query.setFirstResult(offset);
        return query.getResultList();
    }

    @Override
    public List<BaseGame> findByCriteria(QueryCriteria<BaseGame> criteria, String... expansions) {
        expansions = enableExpansions(expansions);
        QueryBuilder<BaseGame, TypedQuery<BaseGame>, TypedQuery<Long>> queryBuilder = new JpaQueryBuilder<BaseGame>(entityManager);
        TypedQuery<BaseGame> query = queryBuilder.getQuery(criteria);
        return query.getResultList();
    }

    @Override
    public Long countByCriteria(QueryCriteria<BaseGame> criteria) {
        QueryBuilder<BaseGame, TypedQuery<BaseGame>, TypedQuery<Long>> queryBuilder = new JpaQueryBuilder<BaseGame>(entityManager);
        TypedQuery<Long> query = queryBuilder.countQuery(criteria);
        return query.getSingleResult();
    }

    @Override
    public List<BaseGame> ownedBy(Account user) {
        TypedQuery<BaseGame> query = entityManager.createQuery(
                "SELECT g FROM BaseGame g WHERE g.owner = :owner", BaseGame.class);
        query.setParameter("owner", user);
        return query.getResultList();
    }

    @Override
    public List<BaseGame> getGamesNear(Coordinates location, Integer limit, Integer offset, String... expansions) {
        String jpql = "SELECT g FROM BaseGame g ORDER BY 3959 * acos( cos(radians(:latitude)) * cos(radians(g.location.latitude)) * cos(radians(g.location.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(g.location.latitude)) ) ASC\n";
        TypedQuery<BaseGame> query = entityManager.createQuery(jpql, BaseGame.class);
        query.setParameter("latitude", location.getLatitude());
        query.setParameter("longitude", location.getLongitude());
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        query.setFirstResult(offset);
        return query.getResultList();
    }

    @Override
    public int count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(g) FROM BaseGame g", Long.class);
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
    public List<PlayerEntity> getPlayersForGame(int gameId, Integer limit, Integer offset) {
        String jpql = "SELECT p "
                + " FROM PlayerEntity p "
                + "     JOIN p.team t "
                + "     JOIN t.game g "
                + " WHERE g.id = :gameId";
        TypedQuery<PlayerEntity> typedQuery = entityManager.createQuery(jpql, PlayerEntity.class)
                .setParameter("gameId", gameId);
        if (limit != null && limit > 0) {
            typedQuery.setMaxResults(limit);
        }
        if (offset != null && offset > 0) {
            typedQuery.setFirstResult(offset);
        }
        return typedQuery.getResultList();
    }

    @Override
    public void setGameAttribute(BaseGame gameEntity, Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
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
    public List<Zone> getZonesForGame(String gameUrl) throws GameNotFoundException {
        List<Zone> zones = get(gameUrl).getZones();
        LOG.debug("Retrieved {} zones", zones.size());
        return zones;
    }

    @Override
    public EventEntity getEvent(int eventId) throws GameEventNotFoundException {
        EventEntity eventEntity = entityManager.find(EventEntity.class, eventId);
        if (eventEntity == null) {
            throw new GameEventNotFoundException("Event for id " + eventId + " not found");
        }
        return eventEntity;
    }

    @Override
    public EventEntity create(EventEntity eventEntity) {
        entityManager.persist(eventEntity);
        return eventEntity;
    }

    @Override
    public EventEntity update(EventEntity eventEntity) {
        return entityManager.merge(eventEntity);
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
