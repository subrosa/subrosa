package com.subrosagames.subrosa.domain.player;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttribute;
import com.subrosagames.subrosa.domain.player.persistence.TargetEntity;
import com.subrosagames.subrosa.domain.player.persistence.TargetPlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TargetTeamEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 */
@Entity
@Table(name = "player")
public class Player extends BaseEntity implements Participant {

    @JsonIgnore
    @Transient
    @Setter
    private PlayerFactory playerFactory;

    @Id
    @SequenceGenerator(name = "playerSeq", sequenceName = "player_player_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playerSeq")
    @Column(name = "player_id")
    @Getter
    @Setter
    private Integer id;

    @NotNull
    @ManyToOne(targetEntity = BaseGame.class)
    @JoinColumn(name = "game_id")
    @Getter
    @Setter
    private Game game;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    @Getter
    @Setter
    private Account account;

    @NotNull
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
    @Getter
    @Setter
    private Map<String, PlayerAttribute> attributes = Maps.newHashMap();

    @ManyToOne
    @JoinColumn(name = "team_id")
    @Getter
    @Setter
    private TeamEntity team;

    @Column(name = "kill_code")
    @Getter
    @Setter
    private String killCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_role")
    @Getter
    @Setter
    private GameRole gameRole;

    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    private List<TargetEntity> targets;

    /**
     * Set the given player attribute.
     *
     * @param fieldId         attribute field it
     * @param playerAttribute attribute to set
     */
    public void setAttribute(String fieldId, PlayerAttribute playerAttribute) {
        attributes.put(fieldId, playerAttribute);
    }

    @Override
    public String getName() {
        return getPlayerProfile().getName();
    }

    @Override
    public Image getAvatar() {
        return getPlayerProfile().getImage();
    }

    @Override
    public Target getTarget(final int targetId) throws TargetNotFoundException {
        return getTargets().stream()
                .filter(t -> t.getId() == targetId).findAny()
                .orElseThrow(() -> new TargetNotFoundException("Target with id " + targetId + " not found for player id " + getId()));
    }

    @Override
    public void addTarget(com.subrosagames.subrosa.domain.player.Player target) {
        TargetPlayerEntity entity = new TargetPlayerEntity();
        entity.setTargetType(TargetType.PLAYER);
        entity.setTarget(target);
        getTargets().add(entity);
    }

    @Override
    public void addTarget(Team target) {
        TargetTeamEntity entity = new TargetTeamEntity();
        entity.setTargetType(TargetType.TEAM);
        entity.setTarget(target);
        getTargets().add(entity);
    }

    @Override
    public void addTarget(Location target) {
    }

    public void update(PlayerDescriptor playerDescriptor) throws AddressNotFoundException, ImageNotFoundException {
        copyPlayerProperties(playerDescriptor);
    }

    void copyPlayerProperties(PlayerDescriptor playerDescriptor) throws AddressNotFoundException, ImageNotFoundException {
        playerFactory.processPlayerAttributes(this, playerDescriptor);
    }

    /**
     * Assert player is valid for storage.
     *
     * @throws PlayerValidationException if player is invalid for storage
     */
    public void assertValid() throws PlayerValidationException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Player>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new PlayerValidationException(violations);
        }
    }

}
