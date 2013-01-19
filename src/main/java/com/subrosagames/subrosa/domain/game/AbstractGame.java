package com.subrosagames.subrosa.domain.game;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.domain.game.persistence.TriggeredEventEntity;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.EventExecutor;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * Interface describing the base level of functionality a game implementation must provide.
 */
public abstract class AbstractGame implements Game {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractGame.class);

    private GameRepository gameRepository;
    private RuleRepository ruleRepository;
    private PlayerFactory playerFactory;
    private EventRepository eventRepository;

    private EventExecutor eventExecutor;

    @JsonIgnore
    private GameEntity gameEntity;
    @JsonIgnore
    private LifecycleEntity lifecycleEntity;


    /**
     * Construct with given game information and lifecycle.
     * @param gameEntity game entity
     * @param lifecycleEntity game lifecycle
     */
    public AbstractGame(GameEntity gameEntity, LifecycleEntity lifecycleEntity) {
        this.gameEntity = gameEntity;
        this.lifecycleEntity = lifecycleEntity;
    }

    /**
     * Get the events that must exist for this to be a valid game.
     * @return array of events
     */
    protected abstract String[] getRequiredEvents();

    /**
     * Get game entity.
     * @return game entity
     */
    public GameEntity getGameEntity() {
        return gameEntity;
    }

    /**
     * Get game lifecycle.
     * @return game lifecycle
     */
    public LifecycleEntity getLifecycleEntity() {
        return lifecycleEntity;
    }

    /**
     * Validate that this is a valid game.
     * @throws GameValidationException if the game is invalid
     */
    public void validate() throws GameValidationException {
    }

    /**
     * Persist this game data as a new game.
     * @throws GameValidationException if the game is invalid
     */
    public void create() throws GameValidationException {
        gameRepository.createGame(this);
    }

    @Override
    public Player getPlayer(int accountId) {
        return new Player(gameRepository.getPlayerForUserAndGame(accountId, this.getId()));
    }

    @Override
    public List<Player> getPlayers() {
        List<PlayerEntity> playerEntities = gameRepository.getPlayersForGame(getId());
        return Lists.transform(playerEntities, new Function<PlayerEntity, Player>() {
            @Override
            public Player apply(@Nullable PlayerEntity input) {
                return playerFactory.getPlayerForEntity(input);
            }
        });
    }

    @Override
    public boolean achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException {
        Target target = player.getTarget(targetId);

        Map<String, Serializable> properties = Maps.newHashMap();
        properties.put("playerId", player.getId());
        properties.put("targetId", targetId);
        eventExecutor.execute(EventMessage.TARGET_ACHIEVED.name(), getId(), properties);
        return true;
    }

    @Transactional
    @Override
    public Player addUserAsPlayer(Account account) {
        return playerFactory.createPlayerForGame(this, account);
    }

    @Transactional
    @Override
    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        LOG.debug("Setting attribute {} to {} for game {}", new Object[] { attributeType, attributeValue, getId() });
        gameRepository.setGameAttribute(getGameEntity(), attributeType, attributeValue);
    }

    @Override
    public void addTriggeredEvent(EventMessage eventType, Event trigger) {
        TriggeredEventEntity entity = eventRepository.createTriggeredEvent(eventType, trigger);
        lifecycleEntity.addTriggeredEvent(entity);
        gameRepository.save(lifecycleEntity);
    }

    @Override
    public List<TriggeredEvent> getEventsTriggeredBy(EventMessage eventMessage) {
        List<TriggeredEvent> eventsTriggered = Lists.newArrayList();
        List<TriggeredEventEntity> persistedEvents = lifecycleEntity.getTriggeredEvents();
        for (TriggeredEventEntity event : persistedEvents) {
            String eventClass = event.getTriggerEvent().getEventClass();
            if (eventClass.equals(eventMessage.name())) {
                eventsTriggered.add(event);
            }
        }
        return eventsTriggered;
    }

    @JsonIgnore
    public List<Post> getPosts() {
        return getGameEntity().getPosts();
    }

    public Map<RuleType, List<String>> getRules() {
        List<Rule> gameRules = getGameEntity().getRules();
        Map<RuleType, List<String>> rules = Maps.newEnumMap(RuleType.class);
        rules.put(RuleType.ALL_GAMES, Lists.transform(ruleRepository.getRulesForType(RuleType.ALL_GAMES), extractRules));
        rules.put(RuleType.GAME_SPECIFIC, Lists.transform(gameRules, extractRules));
        return rules;
    }

    protected Function<Rule, String> extractRules = new Function<Rule, String>() {
        @Override
        public String apply(Rule input) {
            return input.getDescription();
        }
    };

    public int getId() {
        return getGameEntity().getId();
    }

    public String getName() {
        return getGameEntity().getName();
    }

    public String getUrl() {
        return getGameEntity().getUrl();
    }

    public String getDescription() {
        return getGameEntity().getDescription();
    }

    public GameType getGameType() {
        return getGameEntity().getGameType();
    }

    public BigDecimal getPrice() {
        return getGameEntity().getPrice();
    }

    public Date getStartTime() {
        return getLifecycleEntity().getGameStart();
    }

    public Date getEndTime() {
        return getLifecycleEntity().getGameEnd();
    }

    public Date getRegistrationStartTime() {
        return getLifecycleEntity().getRegistrationStart();
    }

    public Date getRegistrationEndTime() {
        return getLifecycleEntity().getRegistrationEnd();
    }

    public String getTimezone() {
        return getGameEntity().getTimezone();
    }

    public String getPassword() {
        return getGameEntity().getPassword();
    }

    public Image getImage() {
        return getGameEntity().getImage();
    }

    public Integer getMinimumAge() {
        return getGameEntity().getMinimumAge();
    }

    public Integer getMaximumTeamSize() {
        return getGameEntity().getMaximumTeamSize();
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

    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}
