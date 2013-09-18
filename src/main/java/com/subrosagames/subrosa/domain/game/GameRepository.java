package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosagames.subrosa.domain.DomainRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository extends DomainRepository<GameEntity> {

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
     * Retrieve the specified game entity by identifying url.
     *
     *
     * @param url game url
     * @param expansions fields to expand
     * @return game
     */
    GameEntity get(String url, String... expansions) throws GameNotFoundException;

    GameEntity get(int id, String... expansions) throws GameNotFoundException;

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

    GameEntity create(GameEntity gameEntity) throws GameValidationException;

    List<GameEntity> ownedBy(Account user);
}
