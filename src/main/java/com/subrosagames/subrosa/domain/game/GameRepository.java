package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.message.Post;

import java.util.List;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository {

    /**
     * Create a game.
     *
     * @param game game information
     * @param gameMaster
     * @return created game
     */
    Game createGame(Game game, Account gameMaster);

    /**
     * Get a list of games, sorted by start date, with the provided limit and offset.
     * @param limit number of games to return
     * @param offset offset into the pool of games
     * @return list of games
     */
    List<Game> getGames(int limit, int offset);

    /**
     * Get a list of games that are currently active.
     * @return active games
     */
    List<Game> getActiveGames();

    /**
     * Get a list of the games that are occurring near the provided geographical location.
     * @param location geographical location
     * @return games near that location
     */
    List<Game> getGamesNear(Coordinates location);

    /**
     * Get the total number of games.
     * @return number of games
     */
    int getGameCount();

    /**
     * Retrieve the specified game by id.
     *
     * @param gameId game id
     * @return game
     */
    GameEntity getGame(int gameId);

    /**
     * Get a list of posts for the specified game.
     * @param gameId game id
     * @param limit number of posts
     * @param offset offset into posts
     * @return list of posts
     */
    List<Post> getPosts(int gameId, int limit, int offset);

    /**
     * Get total count of posts for a give game.
     * @param gameId game id
     * @return post count
     */
    int getPostCount(int gameId);
}
