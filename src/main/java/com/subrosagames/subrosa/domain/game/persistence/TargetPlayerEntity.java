package com.subrosagames.subrosa.domain.game.persistence;

import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressType;
import com.subrosagames.subrosa.domain.game.TargetPlayer;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageType;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 *
 */
@Entity
@Table(name = "target_player")
@DiscriminatorValue("PLAYER")
@PrimaryKeyJoinColumn(name = "target_id")
public class TargetPlayerEntity extends TargetEntity implements TargetPlayer {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private PlayerEntity target;

    public PlayerEntity getTarget() {
        return target;
    }

    public void setTarget(PlayerEntity target) {
        this.target = target;
    }

    @Override
    public Image getPhotoId() {
        return target.getAccount().getImage(ImageType.PHOTO_ID);
    }

    @Override
    public Image getActionPhoto() {
        return target.getAccount().getImage(ImageType.ACTION_PHOTO);
    }

    @Override
    public Image getAvatar() {
        return target.getAccount().getImage(ImageType.AVATAR);
    }

    @Override
    public Address getHomeAddress() {
        return target.getAccount().getAddress(AddressType.HOME);
    }

    @Override
    public Address getWorkAddress() {
        return target.getAccount().getAddress(AddressType.WORK);
    }
}
