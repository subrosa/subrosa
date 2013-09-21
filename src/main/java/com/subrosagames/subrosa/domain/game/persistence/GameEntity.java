package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.GameAttributeValue;
import com.subrosagames.subrosa.domain.game.GameData;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameHelper;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.RuleRepository;
import com.subrosagames.subrosa.domain.game.RuleType;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;
import com.subrosagames.subrosa.util.NullAwareBeanUtilsBean;
import com.subrosagames.subrosa.validation.DateRange;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.Hibernate;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;

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
        })
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GameEntity implements Game, GameData {

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    private static final Integer DEFAULT_MAX_TEAM_SIZE = 0;

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

    @OneToMany(targetEntity = GameEventEntity.class)
    @JoinColumn(name = "game_id")
    @OrderBy("created DESC")
    private List<GameEvent> history;

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

    @Column(name = "registration_start")
    private Date registrationStart;

    @Column(name = "registration_end")
    private Date registrationEnd;

    @Column(name = "game_start")
    private Date gameStart;

    @Column(name = "game_end")
    private Date gameEnd;

    @Column
    private Date published;

    @PrePersist
    void prePersist() {
        if (price == null) {
            price = GameEntity.DEFAULT_PRICE;
        }
        if (maximumTeamSize == null) {
            maximumTeamSize = GameEntity.DEFAULT_MAX_TEAM_SIZE;
        }
    }

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

    @Override
    public Game create() throws GameValidationException {
        assertValid();
        GameEntity gameEntity = gameRepository.create(this);
        gameFactory.injectDependencies(gameEntity);
        return gameEntity;
    }

    @Override
    public Game update(Game game) throws GameValidationException {
        NullAwareBeanUtilsBean beanCopier = new NullAwareBeanUtilsBean();
        try {
            beanCopier.copyProperties(this, game);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        assertValid();
        GameEntity updated;
        try {
            updated = gameRepository.update(this);
        } catch (DomainObjectNotFoundException e) {
            throw new GameValidationException(e);
        } catch (DomainObjectValidationException e) {
            throw new GameValidationException(e);
        }
        gameFactory.injectDependencies(updated);
        return updated;
    }

    @Override
    public Game publish() throws GameValidationException {
        assertValid(PublishAction.class);
        setPublished(new Date());
        try {
            return gameRepository.update(this);
        } catch (DomainObjectNotFoundException e) {
            throw new GameValidationException(e);
        } catch (DomainObjectValidationException e) {
            throw new GameValidationException(e);
        }
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

    @Override
    public Lifecycle getLifecycle() {
        return null;  // TODO
    }

    @Override
    public void addTriggeredEvent(EventMessage eventType, Event trigger) {
        // TODO
    }

    @Override
    public List<TriggeredEvent> getEventsTriggeredBy(EventMessage eventMessage) {
        return null;  // TODO
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

    public List<GameEvent> getHistory() {
        if (!Hibernate.isInitialized(history)) {
            return null;
        }
        return history;
    }

    public void setHistory(List<GameEvent> history) {
        this.history = history;
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
    public Player addUserAsPlayer(Account account) {
        return getGameHelper().addUserAsPlayer(account);
    }

    @Override
    public Player getPlayer(int accountId) {
        return getGameHelper().getPlayer(accountId);
    }

    @Override
    public List<Player> getPlayers() {
        return getGameHelper().getPlayers();
    }

    @Override
    public void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue) {
        getGameHelper().setAttribute(attributeType, attributeValue);
    }

    @Override
    public Post addPost(PostEntity postEntity) {
        postEntity.setGameId(getId());
        return gameRepository.create(postEntity);
    }

    private GameHelper getGameHelper() {
        if (gameHelper == null) {
            gameHelper = new GameHelper(this);
            gameHelper.setGameRepository(gameRepository);
            gameHelper.setPlayerFactory(playerFactory);
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

    public PlayerFactory getPlayerFactory() {
        return playerFactory;
    }
}
