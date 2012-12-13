package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.subrosagames.subrosa.domain.game.*;
import com.subrosagames.subrosa.domain.game.persistence.GameLifecycle;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.event.EventException;
import com.subrosagames.subrosa.event.EventScheduler;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.location.Coordinates;

@Repository
@Transactional
public class JpaGameRepository implements GameRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JpaGameRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private EventScheduler eventScheduler;

    @Override
    public AbstractGame createGame(AbstractGame game) throws GameValidationException {
        GameEntity gameEntity = game.getGameEntity();
        entityManager.persist(gameEntity);

        Lifecycle lifecycle = game.getGameLifecycle();
        entityManager.persist(lifecycle);

        GameLifecycle gameLifecycle = new GameLifecycle();
        gameLifecycle.setGameId(gameEntity.getId());
        gameLifecycle.setLifecycle(lifecycle);
        entityManager.persist(gameLifecycle);

        try {
            eventScheduler.scheduleEvents(game.getGameLifecycle().getScheduledEvents(), game.getId());
        } catch (EventException e) {
            throw new GameValidationException("Failed to schedule game events for game " + gameEntity.getId(), e);
        }
        return game;
    }

    @Override
    public GameEntity getGameEntity(int gameId) {
        LOG.debug("Retrieving game with id {} from the database", gameId);
        return entityManager.find(GameEntity.class, gameId);
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
                return gameFactory.getGameForId(gameEntity.getId());
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
                return gameFactory.getGameForId(gameId);
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

}
