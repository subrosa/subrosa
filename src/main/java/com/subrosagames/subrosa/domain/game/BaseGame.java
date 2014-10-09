package com.subrosagames.subrosa.domain.game;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameEventValidationException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.util.bean.OptionalAwareBeanUtilsBean;

/**
 * Provides common functionality for games.
 */
@Entity
public class BaseGame extends GameEntity implements Game {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);

    @JsonIgnore
    @Transient
    private RuleRepository ruleRepository;
    @JsonIgnore
    @Transient
    private GameRepository gameRepository;
    @JsonIgnore
    @Transient
    private EventRepository eventRepository;
    @JsonIgnore
    @Transient
    private PlayerFactory playerFactory;
    @JsonIgnore
    @Transient
    private GameFactory gameFactory;


    @Transient
    private final Function<Rule, String> extractRules = new Function<Rule, String>() {
        @Override
        public String apply(Rule input) {
            return input.getDescription();
        }
    };

    @Override
    public GameStatus getStatus() {
        Date now = new Date();
        if (Lists.asList(getPublished(), new Date[]{ getRegistrationStart(), getRegistrationEnd(), getGameStart(), getGameEnd() }).contains(null)) {
            return GameStatus.DRAFT;
        } else {
            if (now.before(getRegistrationStart())) {
                return GameStatus.PREREGISTRATION;
            } else if (now.before(getRegistrationEnd())) {
                return GameStatus.REGISTRATION;
            } else if (now.before(getGameStart())) {
                return GameStatus.POSTREGISTRATION;
            } else if (now.before(getGameEnd())) {
                return GameStatus.RUNNING;
            }
        }
        return GameStatus.ARCHIVED;
    }

    @Override
    public Game create() throws GameValidationException {
        assertValid();
        gameRepository.create(this);
        gameFactory.injectDependencies(this);
        return this;
    }

    @Override
    public Game update(GameDescriptor game) throws GameValidationException {
        OptionalAwareBeanUtilsBean beanCopier = new OptionalAwareBeanUtilsBean();
        try {
            beanCopier.copyProperties(this, game);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        assertValid();
        return this;
    }

    @Override
    public Game publish() throws GameValidationException {
        assertValid(PublishAction.class);
        setPublished(new Date());
        try {
            gameRepository.update(this);
        } catch (DomainObjectNotFoundException e) {
            throw new IllegalStateException("Got object not found when updating persisted object");
        } catch (DomainObjectValidationException e) {
            throw new GameValidationException(e);
        }
        return this;
    }

    void assertValid(Class... validationGroups) throws GameValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<BaseGame>> violations = validator.validate(this, validationGroups);
        if (!violations.isEmpty()) {
            throw new GameValidationException(violations);
        }
    }

    @Override
    public GameEvent getEvent(int eventId) throws GameEventNotFoundException {
        EventEntity event = gameRepository.getEvent(eventId);
        // TODO ensure the event is part of this game
        return event;
    }

    @Override
    public Map<RuleType, List<String>> getRules() {
        Map<RuleType, List<String>> rules = Maps.newEnumMap(RuleType.class);
        rules.put(RuleType.ALL_GAMES, Lists.transform(ruleRepository.getRulesForType(RuleType.ALL_GAMES), extractRules));
        final Set<Rule> ruleSet = getRuleSet();
        if (ruleSet != null) {
            rules.put(RuleType.GAME_SPECIFIC, Lists.transform(
                    new ArrayList<Rule>(ruleSet.size()) {{
                        addAll(ruleSet);
                    }},
                    extractRules
            ));
        }
        return rules;
    }

    @Override
    public void achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException {
        player.getTarget(targetId); // throws TargetNotFoundException

        Map<String, Serializable> properties = Maps.newHashMap();
        properties.put("playerId", player.getId());
        properties.put("targetId", targetId);
        properties.put("code", code);
        // TODO actually implement target achieval, whether that be via event or not
//        eventExecutor.execute(EventMessage.TARGET_ACHIEVED.name(), getId(), properties);
    }

    @Override
    public void startGame() {
        throw new NotImplementedException("Attempted to start an abstract game");
    }

    @Override
    public Player addUserAsPlayer(Account account, PlayerDescriptor playerDescriptor) throws PlayerValidationException {
        if (account == null || playerDescriptor == null) {
            throw new IllegalArgumentException("account and playerDescriptor cannot be null");
        }
        return playerFactory.createPlayerForGame(this, account, playerDescriptor);
    }

    @Override
    public Player getPlayer(int accountId) {
        return gameRepository.getPlayerForUserAndGame(accountId, getId());
    }

    @JsonIgnore
    @Override
    public List<Player> getPlayers() {
        return getPlayers(gameRepository.getPlayersForGame(getId()));
    }

    private List<Player> getPlayers(List<? extends Player> players) {
        return Lists.newArrayList(players);
    }

    @Override
    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        LOG.debug("Setting attribute {} to {} for game {}", new Object[]{ attributeType, attributeValue, getId() });
        gameRepository.setGameAttribute(this, attributeType, attributeValue);
    }

    @Override
    public Post addPost(PostEntity postEntity) throws PostValidationException {
        postEntity.setGameId(getId());
        postEntity.setPostType(PostType.TEXT);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PostEntity>> violations = validator.validate(postEntity);
        if (!violations.isEmpty()) {
            throw new PostValidationException(violations);
        }
        return gameRepository.create(postEntity);
    }

    @Override
    public GameEvent addEvent(EventEntity eventEntity) throws GameEventValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventEntity>> violations = validator.validate(eventEntity);
        if (!violations.isEmpty()) {
            throw new GameEventValidationException(violations);
        }
        eventEntity.setGame(this);
        return gameRepository.create(eventEntity);
    }

    public void setGameFactory(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }

    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void setPlayerFactory(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public PlayerFactory getPlayerFactory() {
        return playerFactory;
    }

    public RuleRepository getRuleRepository() {
        return ruleRepository;
    }

    public void setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

}
