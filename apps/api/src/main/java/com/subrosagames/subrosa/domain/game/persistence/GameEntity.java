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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.api.list.Operator;
import com.subrosagames.subrosa.api.list.TimestampToDateTranslator;
import com.subrosagames.subrosa.api.list.annotation.Filterable;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.EnrollmentField;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.Restriction;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.location.persistence.ZoneEntity;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Persisted entity for a game.
 */
@Entity
@Table(name = "game")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "game_type", discriminatorType = DiscriminatorType.STRING)
@NamedEntityGraphs({
        @NamedEntityGraph(name = "posts", attributeNodes = @NamedAttributeNode("posts")),
        @NamedEntityGraph(name = "history", attributeNodes = @NamedAttributeNode("history")),
        @NamedEntityGraph(name = "zones", attributeNodes = @NamedAttributeNode("zones")),
        @NamedEntityGraph(name = "events", attributeNodes = @NamedAttributeNode("events")),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @Getter
    @Setter
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @Getter
    @Setter
    private Account owner;

    @Column
    @Getter
    @Setter
    private String name;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String url;

    @Column
    // TODO use java8 date/time apis
    private Date published;

    @Column
    @Getter
    @Setter
    private String description;

    @Column(name = "game_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private GameType gameType;

    @Column
    @Getter
    @Setter
    private BigDecimal price;

    @Column
    @Getter
    @Setter
    private String timezone;

    @Column(name = "max_team_size")
    @Getter
    @Setter
    private Integer maximumTeamSize;

    @Column
    @Getter
    @Setter
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    @Getter
    @Setter
    private Image image;

    @OneToMany(
            targetEntity = PostEntity.class,
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("created DESC")
    @Getter
    @Setter
    private List<Post> posts;

    @OneToMany(targetEntity = GameHistoryEntity.class)
    @JoinColumn(name = "game_id")
    @OrderBy("created DESC")
    @Getter
    @Setter
    private List<GameHistory> history;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, targetEntity = RuleEntity.class)
    @JoinTable(
            name = "game_rule",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    @Getter
    @Setter
    private Set<Rule> ruleSet = Sets.newHashSet();

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    @MapKey(name = "primaryKey.attributeType")
    @Getter
    @Setter
    private Map<String, GameAttributeEntity> attributes;

    @JsonIgnore
    @OneToMany(targetEntity = ZoneEntity.class)
    @JoinTable(
            name = "game_zone",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    @Getter
    @Setter
    private List<Zone> zones;

    @OneToOne(targetEntity = LocationEntity.class)
    @JoinTable(
            name = "game_location",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    @Getter
    @Setter
    private Location location;

    @JsonIgnore
    @OneToMany(targetEntity = RestrictionEntity.class, mappedBy = "game")
    @Getter
    @Setter
    private Set<Restriction> restrictions;

    @OneToMany(
            targetEntity = EnrollmentFieldEntity.class,
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderColumn(name = "index")
    @Getter
    private List<EnrollmentField> playerInfo = Lists.newArrayList();

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class
    )
    @Getter
    @Setter
    private Date registrationStart;

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class
    )
    @Getter
    @Setter
    private Date registrationEnd;

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class
    )
    @Getter
    @Setter
    private Date gameStart;

    @Filterable(
            operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN },
            translator = TimestampToDateTranslator.class
    )
    @Getter
    @Setter
    private Date gameEnd;

    @OneToMany(
            targetEntity = EventEntity.class,
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Getter
    @Setter
    private List<EventEntity> events;

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

    public Date getPublished() {
        return published == null ? null : new Date(published.getTime());
    }

    public void setPublished(Date published) {
        this.published = published == null ? null : new Date(published.getTime());
    }

    /**
     * Add an enrollment field to the game's required fields.
     *
     * @param enrollmentField enrollment field
     */
    public void addEnrollmentField(EnrollmentField enrollmentField) {
        playerInfo.add(enrollmentField);
    }

}
