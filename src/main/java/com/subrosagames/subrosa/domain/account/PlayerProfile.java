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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Account player profile.
 */
@Entity
@Table(name = "player_profile")
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

    @NotBlank
    @Column
    private String name;

    @NotNull
    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @JsonIgnore
    @OneToMany(
            targetEntity = PlayerEntity.class,
            mappedBy = "playerProfile"
    )
    private Set<Player> players;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }
}
