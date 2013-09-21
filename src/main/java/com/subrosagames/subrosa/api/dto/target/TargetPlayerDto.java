package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.TargetPlayer;

/**
 * Target player DTO.
 */
public class TargetPlayerDto extends TargetDto {

    private Image photoId;
    private Image actionPhoto;
    private Image avatar;
    private Address homeAddress;
    private Address workAddress;

    /**
     * Construct with given target player.
     * @param target target player
     */
    public TargetPlayerDto(TargetPlayer target) {
        super(target);
        photoId = target.getPhotoId();
        actionPhoto = target.getActionPhoto();
        avatar = target.getAvatar();
        homeAddress = target.getHomeAddress();
        workAddress = target.getWorkAddress();
    }

    public Image getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Image photoId) {
        this.photoId = photoId;
    }

    public Image getActionPhoto() {
        return actionPhoto;
    }

    public void setActionPhoto(Image actionPhoto) {
        this.actionPhoto = actionPhoto;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }
}
