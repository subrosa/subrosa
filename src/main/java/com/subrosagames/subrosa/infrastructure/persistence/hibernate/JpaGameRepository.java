package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.AbstractGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameLifecycle;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
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

    @Autowired
    private GameFactory gameFactory;

    @Override
    public AbstractGame createGame(AbstractGame game) throws GameValidationException { // SUPPRESS CHECKSTYLE IllegalType
        GameEntity gameEntity = game.getGameEntity();
        entityManager.persist(gameEntity);

        Lifecycle lifecycle = game.getGameLifecycle();
        entityManager.persist(lifecycle);

        GameLifecycle gameLifecycle = new GameLifecycle();
        gameLifecycle.setGameId(gameEntity.getId());
        gameLifecycle.setLifecycle(lifecycle);
        entityManager.persist(gameLifecycle);

        return game;
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
    public Lifecycle getGameLifecycle(int gameId) {
        return entityManager.find(GameLifecycle.class, gameId).getLifecycle();
    }

    @Override
    public List<Game> getGames(int limit, int offset) {
        LOG.debug("Retrieving game list with limit {} and offset {}", limit, offset);
        TypedQuery<GameEntity> query = entityManager.createQuery("SELECT g FROM GameEntity g", GameEntity.class);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return Lists.transform(query.getResultList(), new Function<GameEntity, Game>() {
            @Override
            public Game apply(GameEntity gameEntity) {
                try {
                    return gameFactory.getGameForId(gameEntity.getId());
                } catch (GameNotFoundException e) {
                    LOG.error("Failed to retrieve a game that just got pulled from the DB! Shenanigans!", e);
                    return null;
                }
            }
        });
    }

    @Override
    public List<Game> getActiveGames() {
        LOG.debug("Retrieving active games list");
        String jpql = "SELECT gl.gameId "
                + " FROM GameLifecycle gl "
                + " JOIN Lifecycle l"
                + " WHERE NOW('') > l.registrationStart "
                + "     AND NOW('') < l.registrationEnd ";
        TypedQuery<Integer> query = entityManager.createQuery(jpql, Integer.class);
        return Lists.transform(query.getResultList(), new Function<Integer, Game>() {
            @Override
            public Game apply(Integer gameId) {
                try {
                    return gameFactory.getGameForId(gameId);
                } catch (GameNotFoundException e) {
                    LOG.error("Failed to retrieve a game that just got pulled from the DB! Shenanigans!", e);
                    return null;
                }
            }
        });
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
}
