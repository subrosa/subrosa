package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory {

    /**
     * Get the game for the given id.
     *
     * @param gameId game id
     * @return game
     * @throws GameNotFoundException if no game exists for that id
     */
    Game getGameForId(int gameId) throws GameNotFoundException;

    /**
     * Create a game with the given game info and lifecycle.
     * @param gameEntity game entity
     * @param lifecycle game lifecycle
     * @return created game
     * @throws GameValidationException if game information is invalid or incomplete
     */
    Game createGame(GameEntity gameEntity, Lifecycle lifecycle) throws GameValidationException;

    /**
     * Get a paginated list of games.
     * @param limit number of games to return
     * @param offset offset into the games list
     * @return paginated list of games
     */
    PaginatedList<Game> getGames(Integer limit, Integer offset);

}
