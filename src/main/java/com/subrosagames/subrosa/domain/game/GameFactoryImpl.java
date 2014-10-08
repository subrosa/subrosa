package com.subrosagames.subrosa.domain.game;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.api.dto.PostDescriptor;
import com.subrosagames.subrosa.domain.BaseDomainObjectFactory;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.gamesupport.GameTypeToEntityMapper;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.event.EventScheduler;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Factory class for generating game domain objects.
 */
@Component
public class GameFactoryImpl extends BaseDomainObjectFactory implements GameFactory {

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
        injectDependencies(game);
        return game;
    }

    void injectDependencies(List<GameEntity> games) {
        for (GameEntity game : games) {
            injectDependencies(game);
        }
    }

    @Override
    public void injectDependencies(GameEntity game) {
        game.setGameRepository(gameRepository);
        game.setGameFactory(this);
        game.setRuleRepository(ruleRepository);
        game.setEventRepository(eventRepository);
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
    public PaginatedList<Game> getGamesNear(Coordinates coordinates, Integer limit, Integer offset, String... expansions) {
        List<GameEntity> gameEntities = gameRepository.getGamesNear(coordinates, limit, offset, expansions);
        List<Game> games = Lists.transform(gameEntities, new Function<GameEntity, Game>() {
            @Override
            public Game apply(GameEntity gameEntity) {
                injectDependencies(gameEntity);
                return gameEntity;
            }
        });
        return new PaginatedList<Game>(
                games,
                gameRepository.count(),
                limit, offset);
    }

    @Override
    public PaginatedList<Game> fromCriteria(QueryCriteria<GameEntity> queryCriteria, String... expansions) {
        List<GameEntity> gameEntities = gameRepository.findByCriteria(queryCriteria, expansions);
        List<Game> games = Lists.transform(gameEntities, new Function<GameEntity, Game>() {
            @Override
            public Game apply(GameEntity gameEntity) {
                injectDependencies(gameEntity);
                return gameEntity;
            }
        });
        return new PaginatedList<Game>(
                games,
                gameRepository.countByCriteria(queryCriteria).intValue(),
                queryCriteria.getLimit(), queryCriteria.getOffset());
    }

    @Override
    public List<? extends Game> ownedBy(Account user) {
        List<GameEntity> gameEntities = gameRepository.ownedBy(user);
        injectDependencies(gameEntities);
        return gameEntities;
    }

    @Override
    public GameEntity forDto(GameDescriptor gameDescriptor) throws GameValidationException {
        GameType gameType;
        if (gameDescriptor.getGameType() == null) {
            gameType = null;
        } else {
            gameType = gameDescriptor.getGameType().orNull();
        }
        GameEntity gameEntity = GameTypeToEntityMapper.forType(gameType);
        copyProperties(gameDescriptor, gameEntity);
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
        return RandomStringUtils.randomAlphanumeric(10);
    }

    @Override
    public PostEntity forDto(PostDescriptor postDescriptor) {
        PostEntity postEntity = new PostEntity();
        copyProperties(postDescriptor, postEntity);
        return postEntity;
    }

    @Override
    public EventEntity forDto(GameEventDescriptor gameEventDescriptor) {
        ScheduledEventEntity eventEntity = new ScheduledEventEntity();
        copyProperties(gameEventDescriptor, eventEntity);
        return eventEntity;
    }

    @Override
    public List<Zone> getGameZones(String gameUrl) throws GameNotFoundException {
        return gameRepository.getZonesForGame(gameUrl);
    }
}
