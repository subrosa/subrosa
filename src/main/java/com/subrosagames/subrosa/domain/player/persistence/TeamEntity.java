package com.subrosagames.subrosa.domain.player.persistence;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * Persisted team entity.
 */
@Entity
@Table(name = "team")
public class TeamEntity {

    @Id
    @SequenceGenerator(name = "teamSeq", sequenceName = "team_team_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teamSeq")
    @Column(name = "team_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    private GameEntity game;

    @Column(name = "game_id")
    private Integer gameId;

    @OneToMany(mappedBy = "team")
    private List<PlayerEntity> players;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column
    private String name;

    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TargetEntity> targets;

    @Column
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity gameId) {
        this.game = gameId;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerEntity> players) {
        this.players = players;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TargetEntity> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetEntity> targets) {
        this.targets = targets;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}
