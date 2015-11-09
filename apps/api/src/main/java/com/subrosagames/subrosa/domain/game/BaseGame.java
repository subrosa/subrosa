package com.subrosagames.subrosa.domain.game;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameAttributeEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.game.validation.GameEventValidationException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.InsufficientInformationException;
import com.subrosagames.subrosa.domain.player.PlayRestrictedException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.domain.player.TeamNotFoundException;
import com.subrosagames.subrosa.domain.validation.VirtualConstraintViolation;
import com.subrosagames.subrosa.event.ScheduledEvent;
import com.subrosagames.subrosa.util.bean.OptionalAwareSimplePropertyCopier;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides common functionality for games.
 */
@Entity
public class BaseGame extends GameEntity implements Game {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);

    private static final int ABSOLUTE_MINIMUM_AGE = 13;

    @JsonIgnore
    @Transient
    @Setter
    private GameRepository gameRepository;
    @JsonIgnore
    @Transient
    @Setter
    private RuleRepository ruleRepository;
    @JsonIgnore
    @Transient
    @Setter
    private PlayerFactory playerFactory;
    @JsonIgnore
    @Transient
    @Setter
    private GameFactory gameFactory;
    @JsonIgnore
    @Transient
    @Setter
    private AccountFactory accountFactory;

    @Override
    public Account getOwner() {
        // TODO we need a more reliable means of domain object DI
        Account owner = super.getOwner();
        accountFactory.injectDependencies(owner);
        return owner;
    }

    @Override
    public GameStatus getStatus() {
        Date now = new Date();
        if (Lists.asList(getPublished(), new Date[] { getRegistrationStart(), getRegistrationEnd(), getGameStart(), getGameEnd() }).contains(null)) {
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
        gameRepository.save(this);
        gameFactory.injectDependencies(this);
        return this;
    }

    @Override
    public Game update(GameDescriptor gameDescriptor) throws GameValidationException, ImageNotFoundException {
        // read-only fields
        gameDescriptor.setId(getId());
        gameDescriptor.setGameType(Optional.of(getGameType()));
        if (isPublished()) {
            gameDescriptor.setUrl(Optional.of(getUrl()));
        }

        GameDescriptorTranslator.ingest(this, gameDescriptor);

        assertValid();
        return this;
    }

    @Override
    public Game publish() throws GameValidationException {
        assertValid(PublishAction.class);
        setPublished(new Date());
        if (getRegistrationStart() == null) {
            setRegistrationStart(getPublished());
        }
        return gameRepository.save(this);
    }

    void assertValid(Class... validationGroups) throws GameValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<BaseGame>> violations = validator.validate(this, validationGroups);
        if (!violations.isEmpty()) {
            throw new GameValidationException(violations);
        }
    }

    @Override
    public EventEntity getEvent(int eventId) throws GameEventNotFoundException {
        return getEvents().stream()
                .filter(e -> e.getId().equals(eventId)).findAny()
                .orElseThrow(() -> new GameEventNotFoundException("no event " + eventId + " for game " + getUrl()));
    }

    @Override
    public Map<RuleType, List<String>> getRules() {
        Map<RuleType, List<String>> rules = Maps.newEnumMap(RuleType.class);
        rules.put(RuleType.ALL_GAMES, Lists.transform(ruleRepository.findAllByRuleType(RuleType.ALL_GAMES), Rule::getDescription));
        rules.put(RuleType.GAME_SPECIFIC, Lists.transform(Lists.newArrayList(getRuleSet()), Rule::getDescription));
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
    public Player joinGame(final Account account, final JoinGameRequest joinGameRequest) throws PlayerValidationException, AddressNotFoundException,
            ImageNotFoundException, PlayerProfileNotFoundException
    {
        checkNotNull(account, "Cannot join game with null account");
        checkNotNull(joinGameRequest, "Cannot join game with null join game request");

        assertRestrictionsSatisfied(account); // throws PlayRestrictedException
        assertEnrollmentFieldsSet(joinGameRequest); // throws InsufficientInformationException

        PlayerDescriptor playerDescriptor = PlayerDescriptor.forEnrollmentFields(getPlayerInfo());
        playerDescriptor.setPlayer(account.getPlayerProfile(joinGameRequest.getPlayerId()));
        playerDescriptor.setAttributes(joinGameRequest.getAttributes());
        return playerFactory.createPlayerForGame(this, account, playerDescriptor);
    }

    @Override
    public Player getPlayer(Integer playerId) throws PlayerNotFoundException {
        return playerFactory.getPlayer(this, playerId);
    }

    @Override
    public Player updatePlayer(Integer playerId, JoinGameRequest joinGameRequest) throws PlayerNotFoundException, AddressNotFoundException,
            ImageNotFoundException, PlayerProfileNotFoundException
    {
        Player player = getPlayer(playerId);
        PlayerDescriptor playerDescriptor = PlayerDescriptor.forEnrollmentFields(getPlayerInfo());
        if (joinGameRequest.getPlayerId() != null && !isInProgress()) {
            playerDescriptor.setPlayer(player.getAccount().getPlayerProfile(joinGameRequest.getPlayerId()));
        }
        playerDescriptor.setAttributes(joinGameRequest.getAttributes());
        player.update(playerDescriptor);
        return player;
    }

    private boolean isInProgress() {
        return getGameStart() != null && getGameStart().before(DateTime.now().toDate());
    }

    @Override
    public boolean isPublished() {
        return getPublished() != null;
    }

    @Override
    public Player getPlayerForUser(int accountId) throws PlayerNotFoundException {
        return getPlayers().stream()
                .filter(p -> p.getAccount().getId().equals(accountId)).findAny()
                .orElseThrow(() -> new PlayerNotFoundException("no player for account " + accountId + " in game " + getUrl()));
    }

    @JsonIgnore
    @Override
    public List<? extends Player> getPlayers(Integer limit, Integer offset) {
        return playerFactory.getPlayers(this, limit, offset);
    }

    @JsonIgnore
    @Override
    public List<? extends Player> getPlayers() {
        return playerFactory.getPlayers(this);
    }

    @JsonIgnore
    @Override
    public List<? extends Team> getTeams() {
        return playerFactory.getTeams(this);
    }

    @Override
    public Team getTeam(Integer teamId) throws TeamNotFoundException {
        return playerFactory.getTeam(this, teamId);
    }

    @Override
    public Team addTeam(TeamDescriptor teamDescriptor) {
        return playerFactory.createTeamForGame(this, teamDescriptor);
    }

    @Override
    public Team updateTeam(Integer teamId, TeamDescriptor teamDescriptor) throws TeamNotFoundException {
        Team team = getTeam(teamId);
        // TODO check constraints against updates - for example, during games
        return team.update(teamDescriptor);
    }

    @Override
    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        LOG.debug("Setting attribute {} to {} for game {}", attributeType, attributeValue, getId());
        GameAttributeEntity attributeEntity = getAttributes().get(attributeType.name());
        if (attributeEntity == null) {
            LOG.debug("Did not find attribute of type {} for game {}. Creating.", attributeType, getId());
            getAttributes().put(attributeType.name(), new GameAttributeEntity(this, attributeType.name(), attributeValue.name()));
        } else {
            LOG.debug("Found attribute of type {} for game {}. Updating.", attributeType, getId());
            attributeEntity.setValue(attributeValue.name());
        }
        gameRepository.save(this);
    }

    @Override
    public Post addPost(PostEntity postEntity) throws PostValidationException {
        postEntity.setGame(this);
        postEntity.setPostType(PostType.TEXT);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PostEntity>> violations = validator.validate(postEntity);
        if (!violations.isEmpty()) {
            throw new PostValidationException(violations);
        }
        getPosts().add(postEntity);
        return postEntity;
    }

    @Override
    public GameEvent addEvent(GameEventDescriptor eventDescriptor) throws GameEventValidationException {
        EventEntity eventEntity = gameFactory.forDto(eventDescriptor);
        eventEntity.setGame(this);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventEntity>> violations = validator.validate(eventEntity);
        if (!violations.isEmpty()) {
            throw new GameEventValidationException(violations);
        }
        getEvents().add(eventEntity);
        return eventEntity;
    }

    @Override
    public GameEvent updateEvent(int eventId, GameEventDescriptor eventDescriptor) throws GameEventNotFoundException, GameEventValidationException {
        EventEntity eventEntity = getEvent(eventId);
        // read-only fields
        eventDescriptor.setId(eventId);

        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(eventEntity, eventDescriptor);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventEntity>> violations = validator.validate(eventEntity);
        if (!violations.isEmpty()) {
            throw new GameEventValidationException(violations);
        }
        return eventEntity;
    }

    private void assertEnrollmentFieldsSet(JoinGameRequest joinGameRequest) throws InsufficientInformationException {
        boolean failed = false;
        Set<ConstraintViolation<Player>> constraints = Sets.newHashSet();
        if (joinGameRequest.getPlayerId() == null) {
            failed = true;
            constraints.add(new VirtualConstraintViolation<>("required", "playerId"));
        }
        for (EnrollmentField enrollmentField : getPlayerInfo()) {
            if (BooleanUtils.isNotFalse(enrollmentField.getRequired()) && !joinGameRequest.getAttributes().containsKey(enrollmentField.getFieldId())) {
                failed = true;
                ConstraintViolation<Player> constraint = new VirtualConstraintViolation<>("required", enrollmentField.getFieldId());
                constraints.add(constraint);
            }
        }
        if (failed) {
            throw new InsufficientInformationException(constraints);
        }
    }

    private void assertRestrictionsSatisfied(Account account) throws PlayRestrictedException {
        boolean failed = false;
        Set<ConstraintViolation<Player>> constraints = Sets.newHashSet();
        if (!DateTime.now().minusYears(ABSOLUTE_MINIMUM_AGE).toDate().after(account.getDateOfBirth())) {
            failed = true;
            ConstraintViolation<Player> constraint = new VirtualConstraintViolation<>(
                    RestrictionType.AGE.message(String.valueOf(ABSOLUTE_MINIMUM_AGE)), RestrictionType.AGE.field());
            constraints.add(constraint);
        }
        if (!failed) {
            for (Restriction restriction : getRestrictions()) {
                if (!restriction.satisfied(account)) {
                    failed = true;
                    ConstraintViolation<Player> constraint = new VirtualConstraintViolation<>(restriction.message(), restriction.field());
                    constraints.add(constraint);
                }
            }
        }
        if (failed) {
            throw new PlayRestrictedException(constraints);
        }
    }

}
