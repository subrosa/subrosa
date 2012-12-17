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
     * @param gameId game id
     * @return game
     */
    Game getGameForId(int gameId);

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

    /**
     * Get a paginated list of the posts for a given game.
     * @param gameId game id
     * @param limit number of posts to return
     * @param offset offset into the posts
     * @return paginated list of posts
     */
    PaginatedList<Post> getPostsForGame(int gameId, int limit, int offset);
}
