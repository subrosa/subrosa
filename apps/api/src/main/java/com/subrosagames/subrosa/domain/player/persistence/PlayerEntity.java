package com.subrosagames.subrosa.domain.player.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.player.GameRole;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerFactory;
import com.subrosagames.subrosa.domain.player.PlayerRepository;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.domain.player.TargetType;
import com.subrosagames.subrosa.domain.player.Team;
import lombok.Getter;
import lombok.Setter;

/**
 * Persists a player.
 */
@Entity
@Table(name = "player")
public class PlayerEntity implements Player {

    @JsonIgnore
    @Transient
    private PlayerRepository playerRepository;
    @JsonIgnore
    @Transient
    private PlayerFactory playerFactory;

    @Id
    @SequenceGenerator(name = "playerSeq", sequenceName = "player_player_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playerSeq")
    @Column(name = "player_id")
    @Getter
    @Setter
    private Integer id;

    @JsonProperty("player")
    @ManyToOne
    @JoinColumn(name = "player_profile_id")
    @Getter
    @Setter
    private PlayerProfile playerProfile;

    @OneToMany(
            mappedBy = "player",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @MapKey(name = "primaryKey.name")
    private Map<String, PlayerAttribute> attributes = Maps.newHashMap();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @Getter
    @Setter
    private TeamEntity team;

    @Column(name = "kill_code")
    @Getter
    @Setter
    private String killCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    @Getter
    @Setter
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_role")
    @Getter
    @Setter
    private GameRole gameRole;

    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TargetEntity> targets;

    @Override
    public void update(PlayerDescriptor playerDescriptor) throws AddressNotFoundException, ImageNotFoundException {
        copyPlayerProperties(playerDescriptor);
    }

    void copyPlayerProperties(PlayerDescriptor playerDescriptor) throws AddressNotFoundException, ImageNotFoundException {
        playerFactory.processPlayerAttributes(this, playerDescriptor);
    }

    public Map<String, PlayerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, PlayerAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Set the given player attribute.
     *
     * @param fieldId         attribute field it
     * @param playerAttribute attribute to set
     */
    public void setAttribute(String fieldId, PlayerAttribute playerAttribute) {
        attributes.put(fieldId, playerAttribute);
    }

    public List<TargetEntity> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetEntity> targets) {
        this.targets = targets;
    }

    @Override
    public Target getTarget(final int targetId) throws TargetNotFoundException {
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
        entity.setTarget(target);
        targets.add(entity);
    }

    @Override
    public void addTarget(Team target) {
        TargetTeamEntity entity = new TargetTeamEntity();
        entity.setTargetType(TargetType.TEAM);
        entity.setTarget(target);
        targets.add(entity);
    }

    @Override
    public void addTarget(Location target) {
    }

    /**
     * Assert player is valid for storage.
     *
     * @throws PlayerValidationException if player is invalid for storage
     */
    public void assertValid() throws PlayerValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PlayerEntity>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new PlayerValidationException(violations);
        }
    }

    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void setPlayerFactory(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
    }

}
