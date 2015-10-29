package com.subrosagames.subrosa.domain.game;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
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
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;
import com.subrosagames.subrosa.domain.validation.VirtualConstraintViolation;
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
        gameRepository.create(this);
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
        rules.put(RuleType.ALL_GAMES, Lists.transform(gameRepository.getRulesForType(RuleType.ALL_GAMES), Rule::getDescription));
        final Set<Rule> ruleSet = getRuleSet();
        if (ruleSet != null) {
            List<Rule> rulesList = new ArrayList<Rule>(ruleSet.size());
            rulesList.addAll(ruleSet);
            rules.put(RuleType.GAME_SPECIFIC, Lists.transform(
                    rulesList,
                    Rule::getDescription
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

    @JsonIgnore
    @Override
    public List<Team> getTeams() {
        return getTeams(gameRepository.getTeamsForGame(getId(), 0, 0));
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

    private List<Team> getTeams(List<? extends Team> teams) {
        return Lists.newArrayList(teams);
    }

    @Override
    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        LOG.debug("Setting attribute {} to {} for game {}", attributeType, attributeValue, getId());
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
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventEntity>> violations = validator.validate(eventEntity);
        if (!violations.isEmpty()) {
            throw new GameEventValidationException(violations);
        }
        return gameRepository.update(eventEntity);
    }

    private void assertEnrollmentFieldsSet(JoinGameRequest joinGameRequest) throws InsufficientInformationException {
        boolean failed = false;
        Set<ConstraintViolation<PlayerEntity>> constraints = Sets.newHashSet();
        if (joinGameRequest.getPlayerId() == null) {
            failed = true;
            constraints.add(new VirtualConstraintViolation<>("required", "playerId"));
        }
        for (EnrollmentField enrollmentField : getPlayerInfo()) {
            if (BooleanUtils.isNotFalse(enrollmentField.isRequired()) && !joinGameRequest.getAttributes().containsKey(enrollmentField.getFieldId())) {
                failed = true;
                ConstraintViolation<PlayerEntity> constraint = new VirtualConstraintViolation<PlayerEntity>("required", enrollmentField.getFieldId());
                constraints.add(constraint);
            }
        }
        if (failed) {
            throw new InsufficientInformationException(constraints);
        }
    }

    private void assertRestrictionsSatisfied(Account account) throws PlayRestrictedException {
        boolean failed = false;
        Set<ConstraintViolation<PlayerEntity>> constraints = Sets.newHashSet();
        if (!DateTime.now().minusYears(ABSOLUTE_MINIMUM_AGE).toDate().after(account.getDateOfBirth())) {
            failed = true;
            ConstraintViolation<PlayerEntity> constraint = new VirtualConstraintViolation<>(
                    RestrictionType.AGE.message(String.valueOf(ABSOLUTE_MINIMUM_AGE)), RestrictionType.AGE.field());
            constraints.add(constraint);
        }
        if (!failed) {
            for (Restriction restriction : getRestrictions()) {
                if (!restriction.satisfied(account)) {
                    failed = true;
                    ConstraintViolation<PlayerEntity> constraint = new VirtualConstraintViolation<>(restriction.message(), restriction.field());
                    constraints.add(constraint);
                }
            }
        }
        if (failed) {
            throw new PlayRestrictedException(constraints);
        }
    }

}
