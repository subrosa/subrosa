package com.subrosagames.subrosa.domain.game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.api.dto.PostDescriptor;
import com.subrosagames.subrosa.api.list.QueryCriteria;
import com.subrosagames.subrosa.domain.BaseDomainObjectFactory;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.game.support.GameTypeToEntityMapper;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.validation.VirtualConstraintViolation;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.JpaQueryBuilder;
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
    private PlayerFactory playerFactory;

    @Autowired
    private AccountFactory accountFactory;

    @Override
    public Game getGame(int id, String... expansions) throws GameNotFoundException {
        return gameRepository.findOne(id, expansions)
                .map(this::injectDependencies)
                .orElseThrow(() -> new GameNotFoundException("no game for id " + id));
    }

    @Override
    public Game getGame(String url, String... expansions) throws GameNotFoundException {
        return gameRepository.findOneByUrl(url, expansions)
                .map(this::injectDependencies)
                .orElseThrow(() -> new GameNotFoundException("no game for url " + url));
    }

    @Override
    public BaseGame injectDependencies(BaseGame game) {
        game.setGameRepository(gameRepository);
        game.setRuleRepository(ruleRepository);
        game.setGameFactory(this);
        game.setPlayerFactory(playerFactory);
        game.setAccountFactory(accountFactory);
        return game;
    }

    @Override
    public PaginatedList<Game> getGamesNear(Coordinates coordinates, Integer limit, Integer offset, String... expansions) {
        int pageNum = offset > 0 && limit > 0 ? offset / limit : 0;
        Page<BaseGame> page = gameRepository.findNearLocation(coordinates, new PageRequest(pageNum, limit), expansions);
        return new PaginatedList<>(
                page.getContent().stream().map(this::injectDependencies).collect(Collectors.toList()),
                page.getTotalElements(),
                limit, offset);
    }

    @Override
    public PaginatedList<Game> fromCriteria(QueryCriteria<BaseGame> queryCriteria, String... expansions) {
        Page<BaseGame> page = gameRepository.findAll(
                (root, query, cb) -> JpaQueryBuilder.getPredicate(queryCriteria, cb, root),
                new PageRequest(queryCriteria.getPageNumber(), queryCriteria.getLimit(), queryCriteria.getSpringDataSort()),
                expansions);
        return new PaginatedList<>(
                page.getContent().stream().map(this::injectDependencies).collect(Collectors.toList()),
                page.getTotalElements(),
                page.getSize(), page.getNumber() * page.getSize());
    }

    @Override
    public List<? extends Game> ownedBy(Account user) {
        return gameRepository.findAllByOwner(user).stream()
                .map(this::injectDependencies)
                .collect(Collectors.toList());
    }

    @Override
    public BaseGame forDto(GameDescriptor gameDescriptor, Account account) throws GameValidationException, ImageNotFoundException {
        GameType gameType = gameDescriptor.getGameType() == null ? null : gameDescriptor.getGameType().orElse(null);
        BaseGame game;
        try {
            game = GameTypeToEntityMapper.forType(gameType);
        } catch (GameTypeUnknownException e) {
            throw new GameValidationException(getVirtualConstraintViolationSet("unknown", "gameType"));
        }
        game.setOwner(account);
        injectDependencies(game);

        GameDescriptorTranslator.ingest(game, gameDescriptor);

        if (game.getUrl() == null) {
            game.setUrl(generateUrl());
        }
        if (game.getTimezone() == null) {
            // TODO set timezone for realz
            game.setTimezone("America/New_York");
        }

        return game;
    }

    private Set<ConstraintViolation<BaseGame>> getVirtualConstraintViolationSet(String constraint, String field) {
        ConstraintViolation<BaseGame> violation = new VirtualConstraintViolation<>(constraint, field);
        Set<ConstraintViolation<BaseGame>> violations = new HashSet<>();
        violations.add(violation);
        return violations;
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
        return gameRepository.findOneByUrl(gameUrl, "zones")
                .orElseThrow(() -> new GameNotFoundException("no game for url " + gameUrl))
                .getZones();
    }

}
