package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.api.dto.PostDescriptor;
import com.subrosagames.subrosa.domain.DomainObjectFactory;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory extends DomainObjectFactory<GameEntity> {

    /**
     * Get the game for the given id.
     *
     * @param gameId     game id
     * @param expansions fields to expand
     * @return game
     * @throws GameNotFoundException if no game exists for that id
     */
    Game getGame(int gameId, String... expansions) throws GameNotFoundException;

    /**
     * Get the game for the given url.
     *
     * @param url        game url
     * @param expansions fields to expand
     * @return game
     * @throws GameNotFoundException if no game exists for that id
     */
    Game getGame(String url, String... expansions) throws GameNotFoundException;

    /**
     * Get a paginated list of games.
     *
     * @param limit      number of games to return
     * @param offset     offset into the games list
     * @param expansions fields to expand
     * @return paginated list of games
     */
    PaginatedList<Game> getGames(Integer limit, Integer offset, String... expansions);

    /**
     * Get the games that the given user has created.
     *
     * @param user game owner
     * @return list of games
     */
    List<? extends Game> ownedBy(Account user);

    /**
     * Creates a game entity for the given game information.
     *
     * @param gameDescriptor game information
     * @return game entity
     * @throws GameValidationException if the entity cannot be created
     */
    GameEntity forDto(GameDescriptor gameDescriptor) throws GameValidationException;

    /**
     * Creates a post entity for the given post information.
     *
     * @param postDescriptor post information
     * @return post entity
     */
    PostEntity forDto(PostDescriptor postDescriptor);

    /**
     * Creates a game event entity for the given event information.
     *
     * @param gameEventDescriptor event information
     * @return event entity
     */
    EventEntity forDto(GameEventDescriptor gameEventDescriptor);

    /**
     * Get the game zones for the specified game.
     *
     * @param gameUrl game url
     * @return list of game zones
     * @throws GameNotFoundException if the specified game does not exist
     */
    List<Zone> getGameZones(String gameUrl) throws GameNotFoundException;

    /**
     * Get games near the given coordinates.
     *
     * @param coordinates coordinates
     * @param limit number of games to return
     * @param offset offset into the games list
     * @param expansions fields to expand
     * @return paginated list of matching games
     */
    PaginatedList<Game> getGamesNear(Coordinates coordinates, Integer limit, Integer offset, String... expansions);

    /**
     * Find games matching the given criteria.
     * @param queryCriteria query criteria
     * @param expansions fields to expand
     * @return list of matching games
     */
    PaginatedList<Game> fromCriteria(QueryCriteria<GameEntity> queryCriteria, String... expansions);
}
