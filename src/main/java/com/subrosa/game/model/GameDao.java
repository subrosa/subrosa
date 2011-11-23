package com.subrosa.game.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    public GameModel getGame(int id) {
        return entityManager.find(GameModel.class, id);
    }

    public List<? extends Game> getGames() {
        return entityManager.createQuery("SELECT g FROM GameModel g").getResultList();
    }
}
