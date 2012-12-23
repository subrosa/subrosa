package com.subrosagames.subrosa.domain.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.game.assassins.AssassinsGame;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.Lifecycle;
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
    private EventScheduler eventScheduler;

    @Autowired
    private PlayerFactory playerFactory;

    @Override
    public Game getGameForId(int gameId) throws GameNotFoundException {
        GameEntity gameEntity = gameRepository.getGameEntity(gameId);
        Lifecycle lifecycle = gameRepository.getGameLifecycle(gameId);
        AssassinsGame game = new AssassinsGame(gameEntity, lifecycle);
        game.setGameRepository(gameRepository);
        game.setRuleRepository(ruleRepository);
        game.setPlayerFactory(playerFactory);
        return game;
    }

    @Override
    public Game createGame(GameEntity gameEntity, Lifecycle lifecycle) throws GameValidationException {
        final AssassinsGame game = new AssassinsGame(gameEntity, lifecycle);
        game.setGameRepository(gameRepository);
        game.setRuleRepository(ruleRepository);
        game.validate();
        game.create();

        LOG.debug("Scheduling events for game {}", game.getId());
        try {
            eventScheduler.scheduleEvent(EventMessage.GAME_START, game.getStartTime(), game.getId());
            eventScheduler.scheduleEvent(EventMessage.GAME_END, game.getEndTime(), game.getId());
            eventScheduler.scheduleEvents(game.getGameLifecycle().getScheduledEvents(), game.getId());
        } catch (EventException e) {
            throw new GameValidationException("Failed to schedule game events for game " + gameEntity.getId(), e);
        }

        return game;
    }

    @Override
    public PaginatedList<Game> getGames(Integer limit, Integer offset) {
        return new PaginatedList<Game>(
                gameRepository.getGames(limit, offset),
                gameRepository.getGameCount(),
                limit, offset);
    }

}
