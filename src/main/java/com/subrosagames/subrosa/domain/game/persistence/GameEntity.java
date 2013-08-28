package com.subrosagames.subrosa.domain.game.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.*;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.Hibernate;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
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
public class GameEntity implements Game {

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

    @Id
    @SequenceGenerator(name = "gameSeq", sequenceName = "game_game_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gameSeq")
    @Column(name = "game_id")
    private int id;

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
    private Set<Rule> ruleList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    @MapKey(name = "primaryKey.attributeType")
    private Map<String, GameAttributeEntity> attributes;

    @JsonIgnore
    @OneToOne(targetEntity = LifecycleEntity.class)
    @JoinTable(
            name = "game_lifecycle",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "lifecycle_id")
    )
    private Lifecycle lifecycle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public Date getStartTime() {
        return getLifecycle().getGameStart();
    }

    @Override
    public Date getEndTime() {
        return getLifecycle().getGameEnd();
    }

    @Override
    public Date getRegistrationStartTime() {
        return getLifecycle().getRegistrationStart();
    }

    @Override
    public Date getRegistrationEndTime() {
        return getLifecycle().getRegistrationEnd();
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public Game create() throws GameValidationException {
        return gameRepository.createGame(this);
    }

    @Override
    public Game publish() throws GameValidationException {
        return null;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
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
    public void addTriggeredEvent(EventMessage eventType, Event trigger) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TriggeredEvent> getEventsTriggeredBy(EventMessage eventMessage) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    public Set<Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(Set<Rule> rulesList) {
        this.ruleList = rulesList;
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
        rules.put(RuleType.GAME_SPECIFIC, Lists.transform(
                new ArrayList<Rule>(ruleList.size()) {{ addAll(ruleList); }},
                extractRules
        ));
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

    private GameHelper getGameHelper() {
        if (gameHelper == null) {
            gameHelper = new GameHelper(this);
            gameHelper.setGameRepository(gameRepository);
            gameHelper.setPlayerFactory(playerFactory);
        }
        return gameHelper;
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
