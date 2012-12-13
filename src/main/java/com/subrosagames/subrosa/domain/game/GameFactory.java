package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameLifecycle;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory {

    /**
     * Create a game domain object for the given stored game entity.
     * @param gameEntity game entity
     * @return game domain object
     */
    Game getGameForEntity(GameEntity gameEntity);

    Game getGameForId(int gameId);

    Game createGame(GameEntity gameEntity, Lifecycle lifecycle) throws GameValidationException;

    PaginatedList<Game> getGames(Integer limit, Integer offset);

    PaginatedList<Post> getPostsForGame(int gameId, int limit, int offset);
}
