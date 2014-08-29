package com.subrosagames.subrosa.domain.game.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.subrosa.api.actions.list.Operator;
import com.subrosa.api.actions.list.TimestampToDateTranslator;
import com.subrosa.api.actions.list.annotation.Filterable;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.*;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.game.validation.GameEventValidationException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.location.persistence.ZoneEntity;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import com.subrosagames.subrosa.util.bean.OptionalAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Hibernate;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Persisted entity for a game.
 */
@Entity
@Table(name = "game")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "game_type", discriminatorType = DiscriminatorType.STRING)
@FetchProfiles({
        @FetchProfile(name = "posts", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = GameEntity.class, association = "posts", mode = FetchMode.JOIN)
        }),
        @FetchProfile(name = "history", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = GameEntity.class, association = "history", mode = FetchMode.JOIN)
        }),
        @FetchProfile(name = "zones", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = GameEntity.class, association = "zones", mode = FetchMode.JOIN)
        }),
        @FetchProfile(name = "events", fetchOverrides = {
                @FetchProfile.FetchOverride(entity = GameEntity.class, association = "events", mode = FetchMode.JOIN)
        })
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GameEntity extends BaseEntity implements Game {

    public static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    public static final Integer DEFAULT_MAX_TEAM_SIZE = 0;

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
    private GameHelper gameHelper;
    @JsonIgnore
    @Transient
    private GameFactory gameFactory;

    @Id
    @SequenceGenerator(name = "gameSeq", sequenceName = "game_game_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gameSeq")
    @Column(name = "game_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Account owner;

    @Column
    private String name;

    @Column
    private String url;

    @Column
    private String description;

    @Column(name = "game_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column
    private BigDecimal price;

    @Column
    private String timezone;

    @Column(name = "max_team_size")
    private Integer maximumTeamSize;

    @Column
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    // TODO move this out of the base model class
    @Column(name = "min_age")
    private Integer minimumAge;

    @OneToMany(targetEntity = PostEntity.class)
    @JoinColumn(name = "game_id")
    @OrderBy("created DESC")
    private List<Post> posts;

    @OneToMany(targetEntity = GameHistoryEntity.class)
    @JoinColumn(name = "game_id")
    @OrderBy("created DESC")
    private List<GameHistory> history;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, targetEntity = RuleEntity.class)
    @JoinTable(
            name = "game_rule",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private Set<Rule> ruleSet = Sets.newHashSet();

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    @MapKey(name = "primaryKey.attributeType")
    private Map<String, GameAttributeEntity> attributes;

    @JsonIgnore
    @OneToMany(targetEntity = ZoneEntity.class)
    @JoinTable(
            name = "game_zone",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private List<Zone> zones;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = LocationEntity.class)
    @JoinTable(
            name = "game_location",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Location location;

    @Filterable(
            operators = {Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN},
            translator = TimestampToDateTranslator.class
    )
    @Column(name = "registration_start")
    private Date registrationStart;

    @Filterable(
            operators = {Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN},
            translator = TimestampToDateTranslator.class
    )
    @Column(name = "registration_end")
    private Date registrationEnd;

    @Filterable(
            operators = {Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN},
            translator = TimestampToDateTranslator.class
    )
    @Column(name = "game_start")
    private Date gameStart;

    @Filterable(
            operators = {Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN},
            translator = TimestampToDateTranslator.class
    )
    @Column(name = "game_end")
    private Date gameEnd;

    @Column
    private Date published;

    @OneToMany(targetEntity = EventEntity.class)
    @JoinTable(
            name = "game_lifecycle",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<GameEvent> events;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public GameStatus getStatus() {
        Date now = new Date();
        if (Lists.asList(published, new Date[]{registrationStart, registrationEnd, gameStart, gameEnd}).contains(null)) {
            return GameStatus.DRAFT;
        } else {
            if (now.before(registrationStart)) {
                return GameStatus.PREREGISTRATION;
            } else if (now.before(registrationEnd)) {
                return GameStatus.REGISTRATION;
            } else if (now.before(gameStart)) {
                return GameStatus.POSTREGISTRATION;
            } else if (now.before(gameEnd)) {
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
        return this;
    }

    private void assertValid(Class... validationGroups) throws GameValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<GameEntity>> violations = validator.validate(this, validationGroups);
        if (!violations.isEmpty()) {
            throw new GameValidationException(violations);
        }
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumTeamSize() {
        return maximumTeamSize;
    }

    public void setMaximumTeamSize(Integer maximumTeamSize) {
        this.maximumTeamSize = maximumTeamSize;
    }

    public List<Post> getPosts() {
        if (!Hibernate.isInitialized(posts)) {
            return null;
        }
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<GameHistory> getHistory() {
        if (!Hibernate.isInitialized(history)) {
            return null;
        }
        return history;
    }

    public void setHistory(List<GameHistory> history) {
        this.history = history;
    }

    public List<GameEvent> getEvents() {
        if (!Hibernate.isInitialized(events)) {
            return null;
        }
        return events;
    }

    public GameEvent getEvent(int eventId) throws GameEventNotFoundException {
        EventEntity event = gameRepository.getEvent(eventId);
        // TODO ensure the event is part of this game
        return event;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }

    public Set<Rule> getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(Set<Rule> rulesList) {
        this.ruleSet = rulesList;
    }

    @Transient
    protected Function<Rule, String> extractRules = new Function<Rule, String>() {
        @Override
        public String apply(Rule input) {
            return input.getDescription();
        }
    };

    public Map<RuleType, List<String>> getRules() {
        Map<RuleType, List<String>> rules = Maps.newEnumMap(RuleType.class);
        rules.put(RuleType.ALL_GAMES, Lists.transform(ruleRepository.getRulesForType(RuleType.ALL_GAMES), extractRules));
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

    public Map<String, GameAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, GameAttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    @Override
    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getGameStart() {
        return gameStart;
    }

    public void setGameStart(Date gameStart) {
        this.gameStart = gameStart;
    }

    public Date getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(Date gameEnd) {
        this.gameEnd = gameEnd;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public RuleRepository getRuleRepository() {
        return ruleRepository;
    }

    public void setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public boolean achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException {
        return getGameHelper().achieveTarget(player, targetId, code);
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
        return getGameHelper().addUserAsPlayer(account, playerDescriptor);
    }

    @Override
    public Player getPlayer(int accountId) {
        return getGameHelper().getPlayer(accountId);
    }

    @JsonIgnore
    @Override
    public List<Player> getPlayers() {
        return getPlayers(getGameHelper().getPlayers());
    }

    private List<Player> getPlayers(List<? extends Player> players) {
        return Lists.newArrayList(players);
    }

    @Override
    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        getGameHelper().setAttribute(attributeType, attributeValue);
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
        GameEvent event = gameRepository.create(eventEntity);
        events.add(event);
        return event;
    }

    private GameHelper getGameHelper() {
        if (gameHelper == null) {
            gameHelper = new GameHelper(this);
            gameHelper.setGameRepository(gameRepository);
            gameHelper.setPlayerFactory(playerFactory);
            gameHelper.setEventRepository(eventRepository);
        }
        return gameHelper;
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
}
