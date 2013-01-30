package com.subrosagames.subrosa.domain.game;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.assassins.AssassinsGame;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.event.EventException;
import com.subrosagames.subrosa.event.EventScheduler;
import com.subrosagames.subrosa.event.message.EventMessage;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Factory class for generating game domain objects.
 */
@Component
public class GameFactoryImpl implements GameFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GameFactoryImpl.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private PlayerFactory playerFactory;

    @Override
    public Game getGameForId(int gameId) throws GameNotFoundException {
        GameEntity gameEntity = gameRepository.getGameEntity(gameId);
        LifecycleEntity lifecycleEntity = gameRepository.getGameLifecycle(gameId);
        AssassinsGame game = new AssassinsGame(gameEntity, lifecycleEntity);
        injectDependencies(game);
        return game;
    }

    @Override
    public Game getGameForUrl(String url) throws GameNotFoundException {
        GameEntity gameEntity = gameRepository.getGameEntity(url);
        LifecycleEntity lifecycleEntity = gameRepository.getGameLifecycle(gameEntity.getId());
        AssassinsGame game = new AssassinsGame(gameEntity, lifecycleEntity);
        injectDependencies(game);
        return game;
    }

    @Override
    public Game createGame(GameEntity gameEntity, LifecycleEntity lifecycleEntity) throws GameValidationException {
        final AssassinsGame game = new AssassinsGame(gameEntity, lifecycleEntity);
        injectDependencies(game);
        game.validate();
        game.create();

        LOG.debug("Scheduling events for game {}", game.getId());
        try {
            eventScheduler.scheduleEvent(EventMessage.GAME_START, game.getStartTime(), game.getId());
            eventScheduler.scheduleEvent(EventMessage.GAME_END, game.getEndTime(), game.getId());
            eventScheduler.scheduleEvents(game.getLifecycleEntity().getScheduledEvents(), game.getId());
        } catch (EventException e) {
            throw new GameValidationException("Failed to schedule game events for game " + gameEntity.getId(), e);
        }

        return game;
    }

    private void injectDependencies(AbstractGame game) {
        game.setGameRepository(gameRepository);
        game.setRuleRepository(ruleRepository);
        game.setPlayerFactory(playerFactory);
    }

    @Override
    public PaginatedList<Game> getGames(Integer limit, Integer offset) {
        List<GameEntity> gameEntities = gameRepository.getGames(limit, offset);
        List<Game> games = Lists.transform(gameEntities, new Function<GameEntity, Game>() {
            @Override
            public Game apply(GameEntity gameEntity) {
                try {
                    return getGameForId(gameEntity.getId());
                } catch (GameNotFoundException e) {
                    LOG.error("Failed to retrieve a game that just got pulled from the DB! Shenanigans!", e);
                    return null;
                }
            }
        });
        return new PaginatedList<Game>(
                games,
                gameRepository.getGameCount(),
                limit, offset);
    }

}
