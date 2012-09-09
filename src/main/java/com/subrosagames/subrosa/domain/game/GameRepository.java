package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.message.Post;

import java.util.List;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository {

    Game createGame(Game game);

    /**
     * Get a list of games, sorted by start date, with the provided limit and offset.
     * @param limit number of games to return
     * @param offset offset into the pool of games
     * @return list of games
     */
    List<Game> getGames(int limit, int offset);

    List<Game> getActiveGames();

    List<Game> getGamesNear(Coordinates location);

    /**
     * Get the total number of games.
     * @return number of games
     */
    int getGameCount();

    /**
     * Retrieve the specified game by id.
     * @param gameId game id
     * @return game
     */
    Game getGame(int gameId);

    List<Post> getPosts(int gameId, int limit, int offset);

    int getPostCount(int gameId);
}
