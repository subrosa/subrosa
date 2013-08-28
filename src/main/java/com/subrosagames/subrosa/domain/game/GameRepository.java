package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
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
    GameEntity createGame(GameEntity game) throws GameValidationException; // SUPPRESS CHECKSTYLE IllegalType

    /**
     * Get a list of games, sorted by start date, with the provided limit and offset.
     *
     *
     * @param limit number of games to return
     * @param offset offset into the pool of games
     * @param expansions
     * @return list of games
     */
    List<GameEntity> getGames(int limit, int offset, String... expansions);

    /**
     * Get a list of games that are currently active.
     * @return active games
     */
    List<Integer> getActiveGames();

    /**
     * Get a list of the games that are occurring near the provided geographical location.
     * @param location geographical location
     * @return games near that location
     */
    List<GameHelper> getGamesNear(Coordinates location);

    /**
     * Get the total number of games.
     * @return number of games
     */
    int getGameCount();

    /**
     * Retrieve the specified game entity by id.
     * @param gameId game id
     * @param expansions fields to expand
     * @return game
     */
    GameEntity getGameEntity(int gameId, String... expansions) throws GameNotFoundException;

    /**
     * Retrieve the specified game entity by identifying url.
     *
     *
     * @param url game url
     * @param expansions fields to expand
     * @return game
     */
    GameEntity getGameEntity(String url, String... expansions) throws GameNotFoundException;

    /**
     * Load the player of a game for the given user.
     * @param accountId account id
     * @param gameId game id
     * @return player entity
     */
    PlayerEntity getPlayerForUserAndGame(int accountId, int gameId);

    /**
     * Get all of the players enrolled in the specified game.
     * @param gameId game id
     * @return list of players
     */
    List<PlayerEntity> getPlayersForGame(int gameId);

    /**
     * Set an attribute of a game.
     * @param gameEntity persisted game
     * @param attributeType attribute type
     * @param attributeValue attribute value
     */
    void setGameAttribute(GameEntity gameEntity, Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue);

    /**
     * Persist the provided game lifecycle.
     * @param lifecycle lifecycle entity
     */
    void save(Lifecycle lifecycle);

}
