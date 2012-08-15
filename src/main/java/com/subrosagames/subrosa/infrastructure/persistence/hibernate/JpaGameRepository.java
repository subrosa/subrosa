package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.message.Post;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 */
@Repository
public class JpaGameRepository implements GameRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Game> getGames(int limit, int offset) {
        Query query = entityManager.createQuery("SELECT g FROM GameModel g", Game.class);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.getResultList();
    }

    @Override
    public int getGameCount() {
        TypedQuery<Integer> query = entityManager.createQuery("SELECT COUNT(1) FROM GameModel", Integer.class);
        return query.getSingleResult();
    }

    @Override
    public Game getGame(int gameId) {
        return entityManager.find(Game.class, gameId);
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
