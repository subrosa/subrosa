package com.subrosagames.subrosa.domain.game;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory {

    /**
     * Get the game for the game entity.
     * @param gameEntity the {@link GameEntity}
     * @return Game {@link Game}
     */
    Game getGameForEntity(GameEntity gameEntity);

}
