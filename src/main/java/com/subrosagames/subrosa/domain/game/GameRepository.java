package com.subrosagames.subrosa.domain.game;

import java.util.List;

import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.message.Post;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository {

    /**
     * Create a game.
     * @param game the {@link Game} to create.
     * @return {@link Game}
     */
    Game createGame(Game game);

    /**
     * Get a list of games, sorted by start date, with the provided limit and offset.
     * @param limit number of games to return
     * @param offset offset into the pool of games
     * @return list of games
     */
    List<Game> getGames(int limit, int offset);

    /**
     * Get a list of active games.
     * @return List of {@link Game}s.
     */
    List<Game> getActiveGames();

    /**
     * Get the games near a location.
     * @param location {@link Coordinates}.
     * @return List of {@link Game}s.
     */
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

    /**
     * Get a list of posts, sorted by post date, with the provided limit and offset.
     * @param gameId the id of the {@link Game}
     * @param limit number of games to return
     * @param offset offset into the pool of games
     * @return list of games
     */
    List<Post> getPosts(int gameId, int limit, int offset);

    /**
     * Get the number of posts in the game.
     * @param gameId the id of the {@link Game}
     * @return int number of posts.
     */
    int getPostCount(int gameId);
}
