package org.eludo.subrosa.game.model;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

/**
 * Provides CRUD functionality for a base game.
 */
@Entity
@Table(name = "game")
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
}
