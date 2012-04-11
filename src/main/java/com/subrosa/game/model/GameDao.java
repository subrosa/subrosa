package com.subrosa.game.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.subrosa.game.Game;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides CRUD functionality for a base game.
 */
@Repository
public class GameDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieve the game with the provided id.
     *
     * @param id game id
     * @return game
     */
    public Game getGame(int id) {
        return entityManager.find(GameModel.class, id);
    }

    public List<Game> getGames(int limit, int offset) {
        Query query = entityManager.createQuery("SELECT g FROM GameModel g");
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.getResultList();
    }
}
