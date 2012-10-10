package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameEntity;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.message.Post;

@Repository
@Transactional
public class JpaGameRepository implements GameRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JpaGameRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GameFactory gameFactory;

    @Override
    public Game createGame(Game game, Account gameMaster) {
        GameEntity gameEntity = new GameEntity();
        BeanUtils.copyProperties(game, gameEntity);
        entityManager.persist(gameEntity);
        return gameFactory.getGameForEntity(gameEntity);
    }

    @Override
    public Game getGame(int gameId) {
        LOG.debug("Retrieving game with id {} from the database", gameId);
        GameEntity gameEntity = entityManager.find(GameEntity.class, gameId);
        return gameFactory.getGameForEntity(gameEntity);
    }

    @Override
    public List<Game> getGames(int limit, int offset) {
        LOG.debug("Retrieving game list with limit {} and offset {}", limit, offset);
        TypedQuery<GameEntity> query = entityManager.createQuery("SELECT g FROM GameEntity g", GameEntity.class);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return Lists.transform(query.getResultList(), new Function<GameEntity, Game>() {
            @Override
            public Game apply(@Nullable GameEntity gameEntity) {
                return gameFactory.getGameForEntity(gameEntity);
            }
        });
    }

    @Override
    public List<Game> getActiveGames() {
        LOG.debug("Retrieving active games list");
        String jpql = "SELECT g FROM GameEntity g "
                + "WHERE NOW() < g.endTime";
        TypedQuery<GameEntity> query = entityManager.createQuery(jpql, GameEntity.class);
        return Lists.transform(query.getResultList(), new Function<GameEntity, Game>() {
            @Override
            public Game apply(@Nullable GameEntity gameEntity) {
                return gameFactory.getGameForEntity(gameEntity);
            }
        });
    }

    @Override
    public List<Game> getGamesNear(Coordinates location) {
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
    public List<Post> getPosts(int gameId, int limit, int offset) {
        TypedQuery<Post> query = entityManager.createQuery("SELECT p FROM Post p WHERE p.gameId = :gameId", Post.class);
        query.setParameter("gameId", gameId);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.getResultList();
    }

    @Override
    public int getPostCount(int gameId) {
        TypedQuery<Integer> query = entityManager.createQuery("SELECT COUNT(1) FROM Post p WHERE p.gameId = :gameId", Integer.class);
        query.setParameter("gameId", gameId);
        return query.getSingleResult();
    }
}
