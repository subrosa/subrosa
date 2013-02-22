package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.GameAttributeValue;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributeEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributePk;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameLifecycle;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

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
    public AbstractGame createGame(AbstractGame game) throws GameValidationException { // SUPPRESS CHECKSTYLE IllegalType
        GameEntity gameEntity = game.getGameEntity();
        entityManager.persist(gameEntity);

        LifecycleEntity seedLifecycle = game.getLifecycleEntity();
        LifecycleEntity lifecycleEntity = new LifecycleEntity();
        lifecycleEntity.setRegistrationStart(seedLifecycle.getRegistrationStart());
        lifecycleEntity.setRegistrationEnd(seedLifecycle.getRegistrationEnd());
        lifecycleEntity.setGameStart(seedLifecycle.getGameStart());
        lifecycleEntity.setGameEnd(seedLifecycle.getGameEnd());
        entityManager.persist(lifecycleEntity);

        List<ScheduledEventEntity> scheduledEvents = seedLifecycle.getScheduledEvents();
        for (ScheduledEventEntity scheduledEvent : scheduledEvents) {
            scheduledEvent.setLifecycle(lifecycleEntity);
            entityManager.persist(scheduledEvent);
        }

        GameLifecycle gameLifecycle = new GameLifecycle();
        gameLifecycle.setGameId(gameEntity.getId());
        gameLifecycle.setLifecycleEntity(lifecycleEntity);
        entityManager.persist(gameLifecycle);

        return game;
    }

    @Override
    public void save(LifecycleEntity lifecycleEntity) {
        entityManager.merge(lifecycleEntity);
    }

    @Override
    public GameEntity getGameEntity(int gameId) throws GameNotFoundException {
        LOG.debug("Retrieving game with id {} from the database", gameId);
        GameEntity gameEntity = entityManager.find(GameEntity.class, gameId);
        if (gameEntity == null) {
            throw new GameNotFoundException("Game for id " + gameId + " not found");
        }
        return gameEntity;
    }

    @Override
    public GameEntity getGameEntity(String url) throws GameNotFoundException {
        LOG.debug("Retrieving game with url {} from the database", url);
        GameEntity gameEntity;
        try {
            gameEntity = entityManager.createQuery("SELECT g FROM GameEntity g WHERE g.url = :url", GameEntity.class)
                    .setParameter("url", url)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new GameNotFoundException("Game for url " + url + " not found");
        }
        return gameEntity;
    }

    @Override
    public LifecycleEntity getGameLifecycle(int gameId) {
        return entityManager.find(GameLifecycle.class, gameId).getLifecycleEntity();
    }

    @Override
    public List<GameEntity> getGames(int limit, int offset) {
        LOG.debug("Retrieving game list with limit {} and offset {}", limit, offset);
        TypedQuery<GameEntity> query = entityManager.createQuery("SELECT g FROM GameEntity g", GameEntity.class);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.getResultList();
    }

    @Override
    public List<Integer> getActiveGames() {
        LOG.debug("Retrieving active games list");
        String jpql = "SELECT gl.gameId "
                + " FROM GameLifecycle gl "
                + " JOIN LifecycleEntity l"
                + " WHERE NOW('') > l.registrationStart "
                + "     AND NOW('') < l.registrationEnd ";
        TypedQuery<Integer> query = entityManager.createQuery(jpql, Integer.class);
        return query.getResultList();
    }

    @Override
    public List<AbstractGame> getGamesNear(Coordinates location) {
        throw new NotImplementedException("Querying for games near a location is not yet supported");
    }

    @Override
    public int getGameCount() {
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
}
