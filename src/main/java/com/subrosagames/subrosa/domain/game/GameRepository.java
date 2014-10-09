package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository extends DomainObjectRepository<BaseGame> {

    /**
     * Get a list of games matching the provided criteria.
     *
     * @param criteria   query criteria
     * @param expansions game field expansions
     * @return matching games
     */
    List<BaseGame> findByCriteria(QueryCriteria<BaseGame> criteria, String... expansions);

    /**
     * Get a count of games matching the provided criteria.
     *
     * @param criteria query criteria
     * @return count of matching games
     */
    Long countByCriteria(QueryCriteria<BaseGame> criteria);

    /**
     * Get a list of the games that are occurring near the provided geographical location.
     *
     * @param location   geographical location
     * @param limit      number of games to return
     * @param offset     offset into games
     * @param expansions fields to expand
     * @return games near that location
     */
    List<BaseGame> getGamesNear(Coordinates location, Integer limit, Integer offset, String... expansions);

    /**
     * Retrieve the specified game entity by identifying url.
     *
     * @param url        game url
     * @param expansions fields to expand
     * @return game
     */
    BaseGame get(String url, String... expansions) throws GameNotFoundException;

    /**
     * Retrieve the specified game entity by id.
     *
     * @param id         game id
     * @param expansions fields to expand
     * @return game
     * @throws GameNotFoundException
     */
    BaseGame get(int id, String... expansions) throws GameNotFoundException;

    /**
     * Load the player of a game for the given user.
     *
     * @param accountId account id
     * @param gameId    game id
     * @return player entity
     */
    PlayerEntity getPlayerForUserAndGame(int accountId, int gameId);

    /**
     * Get all of the players enrolled in the specified game.
     *
     * @param gameId game id
     * @return list of players
     */
    List<PlayerEntity> getPlayersForGame(int gameId);

    /**
     * Set an attribute of a game.
     *
     * @param baseGame       persisted game
     * @param attributeType  attribute type
     * @param attributeValue attribute value
     */
    void setGameAttribute(BaseGame baseGame, Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue);

    BaseGame create(BaseGame baseGame) throws GameValidationException;

    List<BaseGame> ownedBy(Account user);

    PostEntity create(PostEntity postEntity);

    List<Zone> getZonesForGame(String gameUrl) throws GameNotFoundException;

    LocationEntity create(LocationEntity location);

    /**
     * Create a game event.
     *
     * @param eventEntity event entity
     * @return created event
     */
    EventEntity create(EventEntity eventEntity);

    /**
     * Retrieve the specified event.
     *
     * @param eventId event it
     * @return game event
     * @throws GameEventNotFoundException if event does not exist
     */
    EventEntity getEvent(int eventId) throws GameEventNotFoundException;

    /**
     * Update the provided game event.
     *
     * @param eventEntity event entity
     * @return updated event
     */
    EventEntity update(EventEntity eventEntity);
}
