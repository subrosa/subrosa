package com.subrosagames.subrosa.domain.game;

/**
 * Interface for generating game domain objects.
 */
public interface GameFactory {

    Game getGameForEntity(GameEntity gameEntity);

}
