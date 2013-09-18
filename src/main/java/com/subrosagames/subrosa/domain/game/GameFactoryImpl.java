package com.subrosagames.subrosa.domain.game;

import javax.validation.ConstraintViolation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.gamesupport.GameTypeToEntityMapper;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.event.EventScheduler;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.NullAwareBeanUtilsBean;
import com.subrosagames.subrosa.util.RandomString;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.validator.engine.ConstraintViolationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Game getGame(int gameId, String... expansions) throws GameNotFoundException {
        GameEntity game = gameRepository.get(gameId, expansions);
        injectDependencies(game);
        return game;
    }

    @Override
    public Game getGame(String url, String... expansions) throws GameNotFoundException {
        GameEntity game = gameRepository.get(url, expansions);
        game = (GameEntity) getGame(game.getId(), expansions);
        injectDependencies(game);
        return game;
    }

    public void injectDependencies(List<GameEntity> games) {
        for (GameEntity game : games) {
            injectDependencies(game);
        }
    }

    @Override
    public void injectDependencies(GameEntity game) {
        game.setGameRepository(gameRepository);
        game.setGameFactory(this);
        game.setRuleRepository(ruleRepository);
        game.setPlayerFactory(playerFactory);
    }

    @Override
    public PaginatedList<Game> getGames(Integer limit, Integer offset, String... expansions) {
        List<GameEntity> gameEntities = gameRepository.list(limit, offset, expansions);
        List<Game> games = Lists.transform(gameEntities, new Function<GameEntity, Game>() {
            @Override
            public Game apply(GameEntity gameEntity) {
                try {
                    return getGame(gameEntity.getId());
                } catch (GameNotFoundException e) {
                    LOG.error("Failed to retrieve a game that just got pulled from the DB! Shenanigans!", e);
                    return null;
                }
            }
        });
        return new PaginatedList<Game>(
                games,
                gameRepository.count(),
                limit, offset);
    }

    @Override
    public List<? extends Game> ownedBy(Account user) {
        List<GameEntity> gameEntities = gameRepository.ownedBy(user);
        injectDependencies(gameEntities);
        return gameEntities;
    }

    @Override
    public GameEntity forDto(GameDescriptor gameDescriptor) throws GameValidationException {
//        if (gameDescriptor.getGameType() == null) {
//            throw new GameValidationException("Game type must be provided.");
//        }
        GameEntity gameEntity = GameTypeToEntityMapper.forType(gameDescriptor.getGameType());
        NullAwareBeanUtilsBean beanCopier = new NullAwareBeanUtilsBean();
        try {
            beanCopier.copyProperties(gameEntity, gameDescriptor);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        if (gameEntity.getUrl() == null) {
            gameEntity.setUrl(generateUrl());
        }
        if (gameEntity.getTimezone() == null) {
            // TODO set timezone for realz
            gameEntity.setTimezone("America/New_York");
        }
        injectDependencies(gameEntity);
        return gameEntity;
    }

    private String generateUrl() {
        return RandomString.generate(10);
    }
}
