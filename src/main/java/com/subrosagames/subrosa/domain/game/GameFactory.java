package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory {

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
     * Create a game with the given game info and lifecycle.
     *
     *
     * @param game game entity
     * @return created game
     * @throws GameValidationException if game information is invalid or incomplete
     */
    Game createGame(Game game) throws GameValidationException;

    /**
     * Get a paginated list of games.
     * @param limit number of games to return
     * @param offset offset into the games list
     * @return paginated list of games
     */
    PaginatedList<Game> getGames(Integer limit, Integer offset, String... expansions);
}
