package com.subrosagames.subrosa.domain.player.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.player.GameRole;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerRepository;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.TargetType;
import com.subrosagames.subrosa.domain.player.Team;

/**
 * Persists a player.
 */
@Entity
@Table(name = "player")
public class PlayerEntity implements Player {

    @JsonIgnore
    @Transient
    private PlayerRepository playerRepository;

    @Id
    @SequenceGenerator(name = "playerSeq", sequenceName = "player_player_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playerSeq")
    @Column(name = "player_id")
    private Integer id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Column(name = "kill_code")
    private String killCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_role")
    private GameRole gameRole;

    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TargetEntity> targets;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public String getKillCode() {
        return killCode;
    }

    public void setKillCode(String killCode) {
        this.killCode = killCode;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<TargetEntity> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetEntity> targets) {
        this.targets = targets;
    }

    public GameRole getGameRole() {
        return gameRole;
    }

    public void setGameRole(GameRole gameRole) {
        this.gameRole = gameRole;
    }

    @Override
    public Target getTarget(final int targetId) throws TargetNotFoundException {
        List<? extends Target> targets = getTargets();
        Collection<? extends Target> filtered = Collections2.filter(targets, new Predicate<Target>() {
            @Override
            public boolean apply(Target input) {
                return targetId == input.getId();
            }
        });
        if (filtered.size() == 1) {
            return filtered.iterator().next();
        }
        throw new TargetNotFoundException("Target with id " + targetId + " not found for player id " + getId());
    }

    @Override
    public void addTarget(Player target) {
        TargetPlayerEntity entity = new TargetPlayerEntity();
        entity.setTargetType(TargetType.PLAYER);
        entity.setTarget(playerRepository.getPlayer(target.getId()));
        playerRepository.createTarget(entity);

    }

    @Override
    public void addTarget(Team target) {
        TargetTeamEntity entity = new TargetTeamEntity();
        entity.setTargetType(TargetType.TEAM);
        entity.setTarget(playerRepository.getTeam(target.getId()));
        playerRepository.createTarget(entity);
    }

    @Override
    public void addTarget(Location target) {
    }

    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void assertValid() throws PlayerValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PlayerEntity>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new PlayerValidationException(violations);
        }
    }
}
