package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository {

    /**
     * Create a game.
     *
     * @param game game information
     * @return created game
     */
    AbstractGame createGame(AbstractGame game) throws GameValidationException; // SUPPRESS CHECKSTYLE IllegalType

    /**
     * Get a list of games, sorted by start date, with the provided limit and offset.
     *
     * @param limit number of games to return
     * @param offset offset into the pool of games
     * @return list of games
     */
    List<Game> getGames(int limit, int offset);

    /**
     * Get a list of games that are currently active.
     * @return active games
     */
    List<Game> getActiveGames();

    /**
     * Get a list of the games that are occurring near the provided geographical location.
     * @param location geographical location
     * @return games near that location
     */
    List<AbstractGame> getGamesNear(Coordinates location);

    /**
     * Get the total number of games.
     * @return number of games
     */
    int getGameCount();

    /**
     * Retrieve the specified game entity by id.
     * @param gameId game id
     * @return game
     */
    GameEntity getGameEntity(int gameId) throws GameNotFoundException;

    /**
     * Retrieve the lifecycle for the given game id.
     * @param gameId game id
     * @return game lifecycle
     */
    Lifecycle getGameLifecycle(int gameId);

    /**
     * Load the player of a game for the given user.
     * @param accountId account id
     * @param gameId game id
     * @return player entity
     */
    PlayerEntity getPlayerForUserAndGame(int accountId, int gameId);
}
