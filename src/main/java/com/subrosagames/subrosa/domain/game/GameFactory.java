package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.game.post.PostEntity;
import com.subrosagames.subrosa.domain.message.Post;

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

    Post getPostForEntity(PostEntity postEntity);

    Game getGameForId(int gameId);
}
