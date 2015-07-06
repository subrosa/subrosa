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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.Team;
import lombok.Getter;
import lombok.Setter;

/**
 * Persisted team entity.
 */
@Entity
@Table(name = "team")
public class TeamEntity implements Team {

    @Id
    @SequenceGenerator(name = "teamSeq", sequenceName = "team_team_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teamSeq")
    @Column(name = "team_id")
    @Getter
    @Setter
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    @Getter
    @Setter
    private GameEntity game;

    @Column(name = "game_id")
    @Getter
    @Setter
    private Integer gameId;

    @OneToMany(mappedBy = "team")
    @Getter
    @Setter
    private List<PlayerEntity> players;

    @OneToOne
    @JoinColumn(name = "image_id")
    @Getter
    @Setter
    private Image image;

    @Column
    @Getter
    @Setter
    private String name;

    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    private List<TargetEntity> targets;

    @Column
    @Getter
    @Setter
    private String password;

    @Override
    public String identifier() {
        return getId().toString();
    }

    @Override
    public Target getTarget(int targetId) throws TargetNotFoundException {
        return null;
    }

    @Override
    public void addTarget(Player target) {

    }

    @Override
    public void addTarget(Team target) {

    }

    @Override
    public void addTarget(Location target) {

    }

    @Override
    public Image getAvatar() {
        return null;
    }
}
