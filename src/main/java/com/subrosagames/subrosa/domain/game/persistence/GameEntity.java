package com.subrosagames.subrosa.domain.game.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.Rule;
import com.subrosagames.subrosa.domain.game.post.PostEntity;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;

/**
 * Persisted entity for a game.
 */
@Entity
@Table(name = "game")
public class GameEntity {

    @Id
    @SequenceGenerator(name = "gameSeq", sequenceName = "game_game_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gameSeq")
    @Column(name = "game_id")
    private int id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "game_type")
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

    @OneToMany(fetch = FetchType.LAZY, targetEntity = PostEntity.class)
    @JoinColumn(name = "game_id")
    @OrderBy("created desc")
    private List<Post> posts;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = RuleEntity.class)
    @JoinTable(
            name = "game_rule",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private List<Rule> rules;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    @MapKey(name = "primaryKey.attributeType")
    private Map<String, GameAttributeEntity> attributes;

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
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Map<String, GameAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, GameAttributeEntity> attributes) {
        this.attributes = attributes;
    }
}
