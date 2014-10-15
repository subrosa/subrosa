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
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.account.Account;
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
import com.subrosagames.subrosa.domain.player.InsufficientInformationException;
import com.subrosagames.subrosa.domain.player.PlayRestrictedException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.validation.VirtualConstraintViolation;
import com.subrosagames.subrosa.util.bean.OptionalAwareSimplePropertyCopier;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public Game update(GameDescriptor gameDescriptor) throws GameValidationException {
        // read-only fields
        gameDescriptor.setId(getId());
        gameDescriptor.setUrl(Optional.of(getUrl()));
        gameDescriptor.setGameType(Optional.of(getGameType()));

        GameDescriptorTranslator.ingest(this, gameDescriptor);

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
            List<Rule> rulesList = new ArrayList<Rule>(ruleSet.size());
            rulesList.addAll(ruleSet);
            rules.put(RuleType.GAME_SPECIFIC, Lists.transform(
                    rulesList,
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
    public Player joinGame(final Account account, final JoinGameRequest joinGameRequest) throws PlayerValidationException {
        checkNotNull(account, "Cannot join game with null account");
        checkNotNull(joinGameRequest, "Cannot join game with null join game request");

        assertRestrictionsSatisfied(account); // throws PlayRestrictedException
        assertEnrollmentFieldsSet(joinGameRequest); // throws InsufficientInformationException

        PlayerDescriptor playerDescriptor = new PlayerDescriptor();
        playerDescriptor.setName(joinGameRequest.getName());
        playerDescriptor.setAttributes(joinGameRequest.getAttributes());
        return playerFactory.createPlayerForGame(this, account, playerDescriptor);
    }

    @Override
    public Player getPlayerForUser(int accountId) {
        return gameRepository.getPlayerForUserAndGame(accountId, getId());
    }

    @JsonIgnore
    @Override
    public List<Player> getPlayers(Integer limit, Integer offset) {
        return getPlayers(gameRepository.getPlayersForGame(getId(), limit, offset));
    }

    @JsonIgnore
    @Override
    public List<Player> getPlayers() {
        return getPlayers(0, 0);
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
    public GameEvent addEvent(GameEventDescriptor eventDescriptor) throws GameEventValidationException {
        EventEntity eventEntity = gameFactory.forDto(eventDescriptor);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventEntity>> violations = validator.validate(eventEntity);
        if (!violations.isEmpty()) {
            throw new GameEventValidationException(violations);
        }
        eventEntity.setGame(this);
        return gameRepository.create(eventEntity);
    }

    @Override
    public GameEvent updateEvent(int eventId, GameEventDescriptor eventDescriptor) throws GameEventNotFoundException, GameEventValidationException {
        EventEntity eventEntity = gameRepository.getEvent(eventId);
        // read-only fields
        eventDescriptor.setId(eventId);

        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(eventEntity, eventDescriptor);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventEntity>> violations = validator.validate(eventEntity);
        if (!violations.isEmpty()) {
            throw new GameEventValidationException(violations);
        }
        return gameRepository.update(eventEntity);
    }

    @Override
    public Player getPlayer(Integer playerId) throws PlayerNotFoundException {
        return playerFactory.getPlayer(this, playerId);
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

    public void setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    private void assertEnrollmentFieldsSet(JoinGameRequest joinGameRequest) throws InsufficientInformationException {
        boolean failed = false;
        Set<ConstraintViolation<PlayerEntity>> constraints = Sets.newHashSet();
        for (EnrollmentField enrollmentField : getPlayerInfo()) {
            if (!joinGameRequest.getAttributes().containsKey(enrollmentField.getName())) {
                failed = true;
                ConstraintViolation<PlayerEntity> constraint = new VirtualConstraintViolation<PlayerEntity>("required", enrollmentField.getFieldId());
                constraints.add(constraint);
                continue;
            }
            // TODO build out join game with player info requirements
            joinGameRequest.getAttributes().get(enrollmentField.getName());
        }
        if (failed) {
            throw new InsufficientInformationException(constraints);
        }
    }

    private void assertRestrictionsSatisfied(Account account) throws PlayRestrictedException {
        boolean failed = false;
        Set<ConstraintViolation<PlayerEntity>> constraints = Sets.newHashSet();
        for (Restriction restriction : getRestrictions()) {
            if (!restriction.satisfied(account)) {
                failed = true;
                ConstraintViolation<PlayerEntity> constraint = new VirtualConstraintViolation<PlayerEntity>(restriction.message(), restriction.field());
                constraints.add(constraint);
            }
        }
        if (failed) {
            throw new PlayRestrictedException(constraints);
        }
    }

}
