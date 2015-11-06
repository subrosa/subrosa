package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.TargetPlayer;
import lombok.Getter;
import lombok.Setter;

/**
 * Persisted targeted player.
 */
@Entity
@Table(name = "target_player")
@DiscriminatorValue("PLAYER")
@PrimaryKeyJoinColumn(name = "target_id")
public class TargetPlayerEntity extends TargetEntity implements TargetPlayer {

    @OneToOne(fetch = FetchType.EAGER, targetEntity = Player.class)
    @JoinColumn(name = "player_id")
    @Getter
    @Setter
    private Player target;

    @Override
    public Image getAvatar() {
        return target.getAvatar();
    }

    @Override
    public Address getHomeAddress() {
        return null;
    }

    @Override
    public Address getWorkAddress() {
        return null;
    }
}
