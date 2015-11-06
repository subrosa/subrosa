package com.subrosagames.subrosa.domain.account;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.Player;
import lombok.Data;

/**
 * Account player profile.
 */
@Entity
@Table(name = "player_profile")
@Data
public class PlayerProfile {

    @Id
    @SequenceGenerator(name = "playerProfileSeq", sequenceName = "player_profile_player_profile_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playerProfileSeq")
    @Column(name = "player_profile_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @JsonIgnore
    @Column
    private Integer index;

    @NotBlank(groups = { Create.class, Update.class })
    @Column
    private String name;

    @NotNull(groups = { Create.class, Update.class })
    @ManyToOne(optional = true)
    @JoinColumn(name = "image_id", nullable = true) // TODO can we retrofit images to make this non-nullable?
    private Image image;

    @JsonIgnore
    @OneToMany(mappedBy = "playerProfile")
    private Set<Player> players;

    @PrePersist
    @PreUpdate
    private void prepareIndex() {
        if (account != null) {
            index = account.getPlayerProfiles().indexOf(this);
        }
    }

    public interface Create {
    }

    public interface Update {
    }
}
