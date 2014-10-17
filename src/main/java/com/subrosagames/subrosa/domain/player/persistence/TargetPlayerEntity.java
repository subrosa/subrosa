package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressType;
import com.subrosagames.subrosa.domain.player.TargetPlayer;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageType;

/**
 * Persisted targeted player.
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
        return target.getImage(ImageType.PHOTO_ID);
    }

    @Override
    public Image getActionPhoto() {
        return target.getImage(ImageType.ACTION_PHOTO);
    }

    @Override
    public Image getAvatar() {
        return target.getImage(ImageType.AVATAR);
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
