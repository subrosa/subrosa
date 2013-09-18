package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.domain.DomainObjectFactory;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory extends DomainObjectFactory<GameEntity> {

    /**
     * Get the game for the given id.
     * @param gameId game id
     * @param expansions fields to expand
     * @return game
     * @throws GameNotFoundException if no game exists for that id
     */
    Game getGame(int gameId, String... expansions) throws GameNotFoundException;

    /**
     * Get the game for the given url.
     *
     * @param url game url
     * @param expansions fields to expand
     * @return game
     * @throws GameNotFoundException if no game exists for that id
     */
    Game getGame(String url, String... expansions) throws GameNotFoundException;

    /**
     * Get a paginated list of games.
     * @param limit number of games to return
     * @param offset offset into the games list
     * @return paginated list of games
     */
    PaginatedList<Game> getGames(Integer limit, Integer offset, String... expansions);

    /**
     * Get the games that the given user has created.
     * @param user game owner
     * @return list of games
     */
    List<? extends Game> ownedBy(Account user);

    GameEntity forDto(GameDescriptor gameDescriptor) throws GameValidationException;

}
