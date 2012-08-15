package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.message.Post;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/10/12
 * Time: 6:08 午後
 * To change this template use File | Settings | File Templates.
 */
public interface GameRepository {

    List<Game> getGames(int limit, int offset);

    int getGameCount();

    Game getGame(int gameId);

    List<Post> getPosts(int gameId, int limit, int offset);

    int getPostCount(int gameId);

}
