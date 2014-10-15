package com.subrosagames.subrosa.domain.game.persistence;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.Hibernate;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.subrosa.api.actions.list.Operator;
import com.subrosa.api.actions.list.TimestampToDateTranslator;
import com.subrosa.api.actions.list.annotation.Filterable;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.EnrollmentField;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.Restriction;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.location.persistence.ZoneEntity;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.event.ScheduledEvent;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;

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
public class GameEntity extends BaseEntity {

    /**
     * Default price for entrance to a game.
     */
    public static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    /**
     * Default maximum team size.
     */
    public static final Integer DEFAULT_MAX_TEAM_SIZE = 0;

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
    private Date published;

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

    @JsonIgnore
    @OneToMany(targetEntity = RestrictionEntity.class, mappedBy = "game")
    private Set<Restriction> restrictions;

    @OneToMany(
            targetEntity = EnrollmentFieldEntity.class,
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderColumn(name = "index")
    private List<EnrollmentField> playerInfo = Lists.newArrayList();

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class,
            childOperand = "date"
    )
    @OneToMany(targetEntity = ScheduledEventEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    @Where(clause = "event_class='registrationStart'")
    private List<ScheduledEvent> registrationStart;

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class,
            childOperand = "date"
    )
    @OneToMany(targetEntity = ScheduledEventEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    @Where(clause = "event_class='registrationEnd'")
    private List<ScheduledEvent> registrationEnd;

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class,
            childOperand = "date"
    )
    @OneToMany(targetEntity = ScheduledEventEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    @Where(clause = "event_class='gameStart'")
    private List<ScheduledEvent> gameStart;

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class,
            childOperand = "date"
    )
    @OneToMany(targetEntity = ScheduledEventEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    @Where(clause = "event_class='gameEnd'")
    private List<ScheduledEvent> gameEnd;

    @OneToMany(targetEntity = EventEntity.class, mappedBy = "game")
    private List<GameEvent> events;

    /**
     * Set the default price and max team size if unset.
     */
    @PrePersist
    protected void prePersist() {
        super.prePersist();
        setDefaults();
    }

    /**
     * Set the default price and max team size if unset.
     */
    @PreUpdate
    protected void preUpdate() {
        super.preUpdate();
        setDefaults();
    }

    private void setDefaults() {
        if (price == null) {
            price = DEFAULT_PRICE;
        }
        if (maximumTeamSize == null) {
            maximumTeamSize = DEFAULT_MAX_TEAM_SIZE;
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

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }

    public Set<Rule> getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(Set<Rule> ruleSet) {
        this.ruleSet = ruleSet;
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

    public Set<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Set<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public List<EnrollmentField> getPlayerInfo() {
        return playerInfo;
    }

    public void addEnrollmentField(EnrollmentField enrollmentField) {
        playerInfo.add(enrollmentField);
    }

    public Date getGameStart() {
        return gameStart == null ? null :
                gameStart.isEmpty() ? null : gameStart.get(0).getDate();
    }

    public void setGameStart(List<ScheduledEvent> gameStart) {
        this.gameStart = gameStart;
    }

    @JsonIgnore
    public List<ScheduledEvent> getGameStartEvents() {
        return gameStart;
    }

    public Date getGameEnd() {
        return gameEnd == null ? null :
                gameEnd.isEmpty() ? null : gameEnd.get(0).getDate();
    }

    public void setGameEnd(List<ScheduledEvent> gameEnd) {
        this.gameEnd = gameEnd;
    }

    @JsonIgnore
    public List<ScheduledEvent> getGameEndEvents() {
        return gameEnd;
    }

    public Date getRegistrationStart() {
        return registrationStart == null ? null :
                registrationStart.isEmpty() ? null : registrationStart.get(0).getDate();
    }

    public void setRegistrationStart(List<ScheduledEvent> registrationStart) {
        this.registrationStart = registrationStart;
    }

    @JsonIgnore
    public List<ScheduledEvent> getRegistrationStartEvents() {
        return registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd == null ? null :
                registrationEnd.isEmpty() ? null : registrationEnd.get(0).getDate();
    }

    public void setRegistrationEnd(List<ScheduledEvent> registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    @JsonIgnore
    public List<ScheduledEvent> getRegistrationEndEvents() {
        return registrationEnd;
    }

}
