package com.subrosagames.subrosa.domain.game;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory class for generating game domain objects.
 */
@Component
public class GameFactoryImpl implements GameFactory {

    @Autowired
    private GameRepository gameRepository;

    @Override
    public Game getGameForEntity(GameEntity gameEntity) {
        Game game = new AssassinsGame();
        BeanUtils.copyProperties(gameEntity, game);
        return game;
    }

}
