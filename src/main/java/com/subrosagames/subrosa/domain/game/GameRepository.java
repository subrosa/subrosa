package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.domain.DomainRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository extends DomainRepository<GameEntity, GameEntity> {

    /**
     * Get a list of games matching the provided criteria.
     * @param criteria query criteria
     * @param expansions game field expansions
     * @return matching games
     */
    List<GameEntity> findByCriteria(QueryCriteria<GameEntity> criteria, String... expansions);

    /**
     * Get a count of games matching the provided criteria.
     * @param criteria query criteria
     * @return count of matching games
     */
    Long countByCriteria(QueryCriteria<GameEntity> criteria);

    /**
     * Get a list of the games that are occurring near the provided geographical location.
     *
     * @param location geographical location
     * @return games near that location
     */
    List<GameEntity> getGamesNear(Coordinates location, Integer limit, Integer offset, String... expansions);

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

    PostEntity create(PostEntity postEntity);

    List<Zone> getZonesForGame(String gameUrl) throws GameNotFoundException;

    LocationEntity create(LocationEntity location);
}
